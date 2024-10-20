# Robolectric architecture

Robolectric is a unit testing framework that allows Android code to be tested on the JVM without the
need for an emulator or device. This allows tests to run very quickly in a more hermetic
environment. Robolectric has a complex architecture and makes use of many advanced features of the
JVM, such as bytecode instrumentation and custom [`ClassLoader`][class-loader]s. This document
provides a high level overview of Robolectric's architecture.

## Android framework Jars and instrumentation

At the heart of Robolectric are the Android framework Jars and the bytecode instrumentation. The
Android framework Jars are a collection of Jar files that are built directly from Android platform
sources. There is a single Jar file for each version of Android. These Jar files can be built by
checking out an AOSP repo and building the
[robolectric-host-android\_all][robolectric-host-android-all] target. Unlike the `android.jar`
(stubs jar) files managed by Android Studio, which only contain public method signatures, the
Robolectric android-all Jars contain the implementation of the Android Java framework. This gives
Robolectric the ability to use as much real Android code as possible. A new android-all jar is
uploaded to MavenCentral for each Android release. You can see the current android-all
jars [here][android-all-jars].

However, the pristine android-all jars are not the ones used during tests. Instead, Robolectric
modifies the pristine android-all jars using bytecode instrumentation (see
[`ClassInstrumentor`][class-instrumentor]). It performs several modifications:

1. All Android methods, including constructors and static initializers, are
   modified to support `shadowing`. This allows any method call to the Android
   framework to be intercepted by Robolectric and delegated to a shadow method.
   At a high level, this is done by iterating over each Android method and
   converting it into two methods: the original method (but renamed), and the
   `invokedynamic delegator` which can optionally invoke shadow methods if they
   are available.

1. Android constructors are specially modified to create shadow objects if a
   shadow class is bound to the Android class being instantiated.

1. Because the Android version of Java core classes (libcore) contains subtle differences to the
   JDKs, certain problematic method calls have to be intercepted and rewritten. See
   [`AndroidInterceptors`][android-interceptors].

1. Native methods undergo special instrumentation. Currently, native methods are
   converted to no-op non-native methods that are shadowable by default.
   However, there is now a native variant of each method also created. There are 
   more details about native code in a section below.

1. The `final` keyword is stripped from classes and methods.

1. Some bespoke pieces of instrumentation, such as supporting
   [`SparseArray.set()`][sparse-array-set].

This instrumentation is typically performed when a new release of Robolectric is made. These
pre-instrumented Android-all jars are published on MavenCentral. See
the [android-all-instrumented][android-all-instrumented] path. They are lazily downloaded and during
tests runtime using [`MavenArtifactFetcher`][maven-artifact-fetcher].

Although Robolectric supports shadowing for Android framework classes, it is
also possible for users to perform Robolectric instrumentation for any package
(except built-in Java packages). This enables shadowing of arbitrary third-party code.

## Shadows

By default, when an Android method is invoked during a Robolectric test, the real Android framework
code is invoked. This is because a lot of Android framework classes are pure Java code (e.g., the
[`Intent`][intent-source] class or the [`org.json`][org-json-package] package) and that code can run
on the JVM without any modifications needed.

However, there are cases where Robolectric needs to intercept and replace
Android method calls. This most commonly occurs when Android system service or
native methods are invoked. To do this, Robolectric uses a system called Shadow
classes.

Shadow classes are Java classes that contain the replacement code of Android methods when they are
invoked. Each shadow class is bound to specific Android classes and methods through annotations.
There are currently hundreds of shadow classes that can be found [here][shadows-list].

Shadow classes may optionally contain public APIs that can customize the
behavior of the methods they are shadowing.

Robolectric allows tests to specify custom shadows as well to provide user
defined implementation for Android classes.

## Shadow Packages and the Robolectric Annotation Processor 

There are two categories of shadows: Robolectric’s built-in shadows that are aggregated using the
[Robolectric Annotation Processor (RAP)][robolectric-annotation-processor], and custom shadows that
are commonly specified using `@Config(shadows = …)`. RAP is configured to process all the shadow
files that exist in Robolectric’s code. The main shadow package is
[framework shadows][framework-shadows], which contain shadows for the Android framework. There are
other shadow packages in Robolectric's code, such as [httpclient shadows][httpclient-shadows], but
all of them outside of framework shadows are deprecated. When Robolectric is built, each shadow
package is processed by RAP and a [ShadowProvider][shadow-provider] file is generated. For example,
to see the `ShadowProvider` for the framework shadows, you can run:

```shell
./gradlew :shadows:framework:assemble
cat ./shadows/framework/build/generated/src/apt/main/org/robolectric/Shadows.java
```

In this file you will see the class `public class Shadows implements
ShadowProvider`.

During runtime, Robolectric will use `ServiceLoader` to detect all shadow packages
that implement `ShadowProvider` and the shadow classes contained in them.

## `Sandbox` and `ClassLoader`

Before a Robolectric test is executed, a [`Sandbox`][sandbox-source] must be initialized. A
`Sandbox` consists of some high-level structures that are necessary to run a Robolectric test. It
primarily contains a [`SandboxClassLoader`][sandbox-class-loader], which is a custom `ClassLoader`
that is bound to a specific instrumented Android-all jar. Sandboxes also contain the
`ExecutorService` that serves as the main thread (UI thread) as well as high-level instrumentation
configuration. The `SandboxClassLoader` is installed as the default `ClassLoader` for the test
method. When any Android class is requested, `SandboxClassLoader` will attempt to load the Android
class from the instrumented Android-all Jar first. The primary goal of `SandboxClassLoader` is to
ensure that classes from the `android.jar` stubs jar are not inadvertently loaded. When classes from
the `android.jar` stubs jar are loaded, attempting to invoke any method on them will result in a
`RuntimeException("Stub!")` error. Typically, the Android stubs jar is on the classpath during a
Robolectric test, but it is important not to load classes from the stubs jar.

## Invokedynamic Delegators and `ShadowWrangler`

This section provides more detail for `invokedynamic delegators` that were referenced in the
instrumentation section. For an overview of the `invokedynamic` JVM instructions, you can search for
articles or watch [YouTube videos such as this][invoke-dynamic-video].

To reiterate, for any Android method, Robolectric’s instrumentation adds an
`invokedynamic delegator` that is responsible for determining at runtime to either invoke the real
Android framework code or a shadow method. The first time an Android method is invoked in a
`Sandbox`, it will result in a call to one of the bootstrap methods in
[`InvokeDynamicSupport`][invoke-dynamic-support]. This will subsequently invoke the
[`ShadowWrangler.findShadowMethodHandle()`][shadow-wrangler-find-shadow-method-handle] to determine
if a shadow method exists for the method that is being invoked. If a shadow method is available, a
`MethodHandle` to it will be returned. Otherwise, a `MethodHandle` for the original framework code
will be returned.

## Test lifecycle

There is a lot of work done by Robolectric before and after a test is run. Besides the `Sandbox` and
`ClassLoader` initialization mentioned above, there is also extensive Android environment
initialization that occurs before each test. The high-level class for this is
[`AndroidTestEnvironment`][android-test-environment]. This involves:

* Initializing up the `Looper` mode (i.e., the scheduler)
* Initializing system and app resources
* Initializing the application context and system context
* Loading the Android manifest for the test
* Creating the `Application` object used for the test
* Initializing the [display configuration](device-configuration.md)
* Setting up the `ActivityThread`
* Creating app directories

It is possible for users to extend the test environment setup using
[`TestEnvironmentLifecyclePlugin`][test-environment-lifecycle-plugin].

Similarly, after each test, many Android classes are reset during
[`RobolectricTestRunner.finallyAfterTest()`][robolectric-test-runner-finally-after-test]. This will
iterate over all shadows and invoke their static `@Resetter` methods.

## Plugin system

Many parts of Robolectric can be customized using a plugin system based on Java’s
[`ServiceLoader`][service-loader]. This extensibility is useful when running Robolectric in more
constrained environments. For example, by default, most of the Robolectric classes are designed to
work in a Gradle/Android Studio environment. However, there are companies (such as Google) that use
alternate build systems (such as Bazel), and it can be helpful to be able to customize the behavior
of some core modules.

The [`pluginapi`][plugin-api] subproject contains many extension points of Robolectric. However,
virtually any class that is loaded by Robolectric’s [`Injector`][injector-source] has the ability to
use [`PluginFinder`][plugin-finder], which means it can be extended at runtime.

Typically, `ServiceLoaders` plugins can be easily written using the [`AutoService`][auto-service]
project.

[android-all-jars]: https://repo1.maven.org/maven2/org/robolectric/android-all
[android-all-instrumented]: https://repo1.maven.org/maven2/org/robolectric/android-all-instrumented
[android-interceptors]: https://github.com/robolectric/robolectric/blob/master/sandbox/src/main/java/org/robolectric/interceptors/AndroidInterceptors.java
[android-test-environment]: https://github.com/robolectric/robolectric/blob/master/robolectric/src/main/java/org/robolectric/android/internal/AndroidTestEnvironment.java
[auto-service]: https://github.com/google/auto/tree/main/service
[class-instrumentor]: https://github.com/robolectric/robolectric/blob/master/sandbox/src/main/java/org/robolectric/internal/bytecode/ClassInstrumentor.java
[class-loader]: https://docs.oracle.com/javase/8/docs/api/java/lang/ClassLoader.html
[framework-shadows]: https://github.com/robolectric/robolectric/tree/master/shadows/framework
[httpclient-shadows]: https://github.com/robolectric/robolectric/tree/master/shadows/httpclient
[injector-source]: https://github.com/robolectric/robolectric/blob/master/utils/src/main/java/org/robolectric/util/inject/Injector.java
[intent-source]: https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/content/Intent.java
[invoke-dynamic-support]: https://github.com/robolectric/robolectric/blob/master/sandbox/src/main/java/org/robolectric/internal/bytecode/InvokeDynamicSupport.java
[invoke-dynamic-video]: https://www.youtube.com/watch?v=KhiECfzyVt0
[maven-artifact-fetcher]: https://github.com/robolectric/robolectric/blob/master/plugins/maven-dependency-resolver/src/main/java/org/robolectric/internal/dependency/MavenArtifactFetcher.java
[org-json-package]: https://cs.android.com/android/platform/superproject/main/+/main:libcore/json/src/main/java/org/json
[plugin-api]: https://github.com/robolectric/robolectric/tree/master/pluginapi
[plugin-finder]: https://github.com/robolectric/robolectric/blob/master/utils/src/main/java/org/robolectric/util/inject/PluginFinder.java
[robolectric-annotation-processor]: https://github.com/robolectric/robolectric/blob/master/processor/src/main/java/org/robolectric/annotation/processing/RobolectricProcessor.java
[robolectric-host-android-all]: https://cs.android.com/android/platform/superproject/main/+/main:external/robolectric/Android.bp;l=112
[robolectric-test-runner-finally-after-test]: https://github.com/robolectric/robolectric/blob/master/robolectric/src/main/java/org/robolectric/RobolectricTestRunner.java#L299
[sandbox-class-loader]: https://github.com/robolectric/robolectric/blob/master/sandbox/src/main/java/org/robolectric/internal/bytecode/SandboxClassLoader.java
[sandbox-source]: https://github.com/robolectric/robolectric/blob/master/sandbox/src/main/java/org/robolectric/internal/bytecode/Sandbox.java
[service-loader]: https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html
[shadow-provider]: https://github.com/robolectric/robolectric/blob/master/shadowapi/src/main/java/org/robolectric/internal/ShadowProvider.java
[shadow-wrangler-find-shadow-method-handle]: https://github.com/robolectric/robolectric/blob/master/sandbox/src/main/java/org/robolectric/internal/bytecode/ShadowWrangler.java#L170
[shadows-list]: https://github.com/robolectric/robolectric/tree/master/shadows/framework/src/main/java/org/robolectric/shadows
[sparse-array-set]: https://github.com/robolectric/robolectric/blob/master/sandbox/src/main/java/org/robolectric/internal/bytecode/ClassInstrumentor.java#L204
[test-environment-lifecycle-plugin]: https://github.com/robolectric/robolectric/blob/master/pluginapi/src/main/java/org/robolectric/pluginapi/TestEnvironmentLifecyclePlugin.java
