# Getting Started

Robolectric works with various build systems, which are documented on this page. If you are starting
a new project, we recommend [Gradle][gradle] as a first choice, since it is the default build system
for Android.

## Building with Gradle

Start by adding the following to your module's `build.gradle`/`build.gradle.kts` file:

/// tab | Groovy

```groovy
android {
  testOptions {
    unitTests {
      includeAndroidResources = true
    }
  }
}

dependencies {
  testImplementation 'junit:junit:4.13.2'
  testImplementation 'org.robolectric:robolectric:{{ robolectric.version.current }}'
}
```

///

/// tab | Kotlin

```kotlin
android {
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
}

dependencies {
  testImplementation("junit:junit:4.13.2")
  testImplementation("org.robolectric:robolectric:{{ robolectric.version.current }}")
}
```

///

Then, mark your test to run with `RobolectricTestRunner`:

/// tab | Java

```java
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SandwichTest {
}
```

///

/// tab | Kotlin

```kotlin
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SandwichTest
```

///

## Building with Bazel

Robolectric works with [Bazel][bazel] 0.10.0 or higher. Bazel integrates with Robolectric through
the `android_local_test` rule. The Robolectric Java/Kotlin test code is the same for a Bazel project
as for a Gradle project (see section above).

Robolectric needs to be added as a dependency to your Bazel project with
[`rules_jvm_external`][bazel-rules-jvm-external]. Add the following to your `WORKSPACE` file:

```python
http_archive(
    name = "robolectric",
    sha256 = "f7b8e08f246f29f26fddd97b7ab5dfa01aaa6170ccc24b9b6a21931bde01ad6f",
    strip_prefix = "robolectric-bazel-4.12.2",
    urls = ["https://github.com/robolectric/robolectric-bazel/releases/download/4.12.2/robolectric-bazel-4.12.2.tar.gz"],
)
load("@robolectric//bazel:robolectric.bzl", "robolectric_repositories")

robolectric_repositories()

http_archive(
    name = "rules_jvm_external",
    sha256 = "d31e369b854322ca5098ea12c69d7175ded971435e55c18dd9dd5f29cc5249ac",
    strip_prefix = "rules_jvm_external-5.3",
    url = "https://github.com/bazelbuild/rules_jvm_external/releases/download/5.3/rules_jvm_external-5.3.tar.gz",
)
load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    name = "maven",
    artifacts = [
        "com.google.truth:truth:1.1.3",
        "org.robolectric:robolectric:{{ robolectric.version.current }}",
    ],
    repositories = [
        "https://maven.google.com",
        "https://repo1.maven.org/maven2",
    ],
)
```

Add an `android_local_test` rule to your `BUILD` file:

```python
android_local_test(
    name = "greeter_activity_test",
    srcs = ["GreeterTest.java"],
    manifest = "TestManifest.xml",
    test_class = "com.example.bazel.GreeterTest",
    deps = [
        ":greeter_activity",
        "@maven//:org_robolectric_robolectric",
        "@robolectric//bazel:android-all",
    ],
)
```

> [!NOTE]
> These instructions use `robolectric-bazel` 4.12.2 and `rules_jvm_external` 5.3. Please check
> [`robolectric-bazel`'s latest release][bazel-latest-release] for up-to-date information.

If you have any question about Bazel integration, we recommend to [check
`robolectric-bazel`][robolectric-bazel] first, and file an issue there if you need assistance.

## Building with Maven

Start by adding the following to your module's `pom.xml` file:

```xml
<dependency>
    <groupId>org.robolectric</groupId>
    <artifactId>robolectric</artifactId>
    <version>{{ robolectric.version.current }}</version>
    <scope>test</scope>
</dependency>
```

Then, mark your test to run with `RobolectricTestRunner`:

/// tab | Java

```java
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SandwichTest {
}
```

///

/// tab | Kotlin

```kotlin
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SandwichTest
```

///

### Using libraries

If you use Maven to build your application, you will need to tell Robolectric where the unpacked
resources are located for each library you use. This can either be specified with the `@Config`
annotation:

/// tab | Java

```java
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@Config(libraries = {
  "build/unpacked-libraries/library1",
  "build/unpacked-libraries/library2"
})
public class SandwichTest {
}
```

///

/// tab | Kotlin

```kotlin
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@Config(libraries = [
  "build/unpacked-libraries/library1",
  "build/unpacked-libraries/library2"
])
class SandwichTest
```

///

or specified in the [`robolectric.properties`](configuring.md/#robolectricproperties-file) file:

```properties
libraries=build/unpacked-libraries/library1,build/unpacked-libraries/library2
```

All paths are relative to the root directory of the project.

### Debugging resource loading issues

If you are not sure if resources are being loaded for a particular library, enable debug logging by
setting the system property `robolectric.logging.enabled = true` and run your tests. You should see
lots of output like:

```text
Loading resources for 'com.foo' from build/unpacked-libraries/library1...
```

If you don't see a particular library in the list, double-check the configuration.

If you reference resources that are outside of your project (i.e. in an AAR dependency), you will
need to provide Robolectric with a pointer to the exploded AAR in your build system. See
[using libraries](#using-libraries) above for more information.

## Additional resources

* [Build system integration](build-system-integration.md)
* [Build with Buck](https://buckbuild.com/rule/robolectric_test.html)
* [Build with Buck2](https://buck2.build/docs/api/rules/#robolectric_test)
* [Android's Testing samples](https://github.com/android/testing-samples)

[bazel]: https://bazel.build
[bazel-latest-release]: https://github.com/robolectric/robolectric-bazel/releases/latest
[bazel-rules-jvm-external]: https://github.com/bazelbuild/rules_jvm_external
[gradle]: https://gradle.org/
[robolectric-bazel]: https://github.com/robolectric/robolectric-bazel
