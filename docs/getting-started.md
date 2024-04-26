# Getting Started

Robolectric works best with Gradle or Bazel. If you are starting a new project, we would recommend Gradle first (since it is the build system of choice in Android Studio) and Bazel second. Both environments provide first class support for Robolectric. If you are using another build system see how to configure [other environments](other-environments.md) or learn how to provide first class [build system integration](build-system-integration.md) support for other environments.

## Building with Android Studio/Gradle

Robolectric works best with Android Studio and [Android Gradle Plugin 3.2.1](https://developer.android.com/studio/releases/gradle-plugin#updating-plugin) or newer.

Add the following to your `build.gradle`:

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

Add this line to your `gradle.properties` (no longer necessary with Android Studio 3.3+):

```properties
android.enableUnitTestBinaryResources=true
```

Annotate your test with the Robolectric test runner:

```java
@RunWith(RobolectricTestRunner.class)
public class SandwichTest {
}
```

## Building with Bazel

Robolectric works with [Bazel](https://bazel.build) 0.10.0 or higher. Bazel integrates with Robolectric through the `android_local_test` rule. The Robolectric Java test code is the same for a Bazel project as a new Gradle project.

Robolectric needs to be added as a dependency to your Bazel project with [`rules_jvm_external`](https://github.com/bazelbuild/rules_jvm_external). Add the following to your WORKSPACE file:

```python
http_archive(
    name = "robolectric",
    urls = ["https://github.com/robolectric/robolectric-bazel/archive/4.7.3.tar.gz"],
    strip_prefix = "robolectric-bazel-4.7.3",
)
load("@robolectric//bazel:robolectric.bzl", "robolectric_repositories")
robolectric_repositories()

http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-4.2",
    sha256 = "cd1a77b7b02e8e008439ca76fd34f5b07aecb8c752961f9640dea15e9e5ba1ca",
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/4.2.zip",
)
load("@rules_jvm_external//:defs.bzl", "maven_install")
maven_install(
    artifacts = [
        "org.robolectric:robolectric:4.7.3",
    ],
    repositories = [
        "https://maven.google.com",
        "https://repo1.maven.org/maven2",
    ],
)
```

Add an `android_local_test` rule to your BUILD file:

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

[robolectric-bazel](https://github.com/robolectric/robolectric-bazel) repository has latest integration manual for Bazel. If you have any question about Bazel integration, we recommend to check it firstly, and file an issue if it doesn't resolve your problem.

## Other Environments

* [Buck](https://buckbuild.com/rule/robolectric_test.html)
* [Older Android Studio/Gradle Versions](other-environments.md#android-studio-gradle-agp-30)
* [Maven & Eclipse](other-environments.md#maven-eclipse)

## Sample Projects

Look at the [Google's Android Testing samples](https://github.com/googlesamples/android-testing) to see how fast and easy it can be to test drive the development of Android applications.
