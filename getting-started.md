---
title: Getting Started
group: Setup
order: 1
---

# Getting Started

Robolectric works best with Gradle or Bazel. If you are starting a new project, we would recommend Gradle first (since it is the build system of choice in Android Studio) and Bazel second. Both environments provide first class support for Robolectric. If you are using another build system see how to configure [other environments](http://robolectric.org/other-environments) or learn how to provide first class [build system integration](http://robolectric.org/build-system-integration) support for other environments.

## Building with Gradle

### Android Studio 3.0+

Starting with Robolectric 3.3 there is now tighter integration with the tool chain, where the build system processes and merges your resources. You'll need Android Studio 3.0.

Add the following to your build.gradle:

```groovy
testImplementation "org.robolectric:robolectric:{{ site.robolectric.version.current | escape }}"

android {
  testOptions {
    unitTests {
      includeAndroidResources = true
    }
  }
}
```

Annotate your test with the Robolectric test runner:

```java
@RunWith(RobolectricTestRunner.class)
public class SandwichTest {
}
```

## Building with Bazel
Robolectric works with [Bazel](https://bazel.build) 0.10.0 or higher. Bazel integrates with Robolectric through the android_local_test rule. The Robolectric Java test code is the same for a Bazel project as a new Gradle project.

Robolectric needs to be added as a dependency to your Bazel project. Add the following to your WORKSPACE file:
```python
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
 name = "robolectric",
 urls = ["https://github.com/robolectric/robolectric-bazel/archive/4.0.1.tar.gz"],
 strip_prefix = "robolectric-bazel-4.0.1",
 sha256 = "dff7a1f8e7bd8dc737f20b6bbfaf78d8b5851debe6a074757f75041029f0c43b",
)
load("@robolectric//bazel:setup_robolectric.bzl", "robolectric_repositories")
robolectric_repositories()
```

Robolectric version 3.8 is also supported.

Add an android_local_test rule to your BUILD file:
```python
android_local_test(
  name = "MyTest",
  srcs = ["MyTest.java"],
  manifest = "TestManifest.xml",
  deps = [
    ":sample_test_lib",
    "@robolectric//bazel:robolectric",
  ],
)

android_library(
    name = "sample_test_lib",
    srcs = ["Lib.java"],
    resource_files = glob(["res/**"]),
    manifest = "AndroidManifest.xml",
)
```

## Sample Projects

Look at the [Robolectric samples](https://github.com/robolectric/robolectric-samples) to see how fast and easy it can be to test drive the development of Android applications. In addition, check out the [Gradle](https://github.com/robolectric/deckard-gradle) or [Maven](https://github.com/robolectric/deckard-maven) starter projects.
