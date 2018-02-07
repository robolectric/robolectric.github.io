---
title: Getting Started
group: Setup
order: 1
---

# Getting Started

Robolectric works best with Gradle or Bazel. If you are starting a new project, we would recommend Gradle first (since it is the build system of choice in Android Studio) and Bazel second. Both environments provide first class support for Robolectric. If you are using another build system see how to configure [other environments](/other-environments.md) or learn how to provide first class [build system integration](/build-system-integration.md) support for other environments.

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

## Sample Projects

Look at the [Robolectric samples](https://github.com/robolectric/robolectric-samples) to see how fast and easy it can be to test drive the development of Android applications. In addition, check out the [Gradle](https://github.com/robolectric/deckard-gradle) or [Maven](https://github.com/robolectric/deckard-maven) starter projects.
