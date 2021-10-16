---
title:  "sharedTest pattern: sharing tests and speeding up development"
author: utzcoz
---

After [Robolectric's 4.0 release][2], `Robolectric` supports the [`AndroidJUnit4` test runner][3], [`ActivityScenario`][5], and [Espresso][4] for interacting with UI components. As we know, we also can run those tests with an official emulator. This article will show an often overlooked but widely-used pattern called sharedTest to share tests between local and instrumentation tests. This will provide the benefit of fast unit testing while ensuring that tests are high-fidelity by enabling them to be run in an emulator.

## Using sharedTest steps by steps
The first thing that sharedTest needs is [`AndroidJUnit4` test runner][3]. It is a test runner that supports both `Robolectric` and [`androidx.test`][13]. There is a sample class, called [`SampleFragmentTest.kt`][14] from [FragmentScenarioSample][15] that uses [`AndroidJUnit4` test runner][3]:

```kotlin
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode

/**
 * A test using the androidx.test unified API, which can execute on an Android device or locally using Robolectric.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SampleFragmentTest {
    @Test
    fun launchFragmentAndVerifyUI() {
        // use launchInContainer to launch the fragment with UI
        launchFragmentInContainer<SampleFragment>()

        // now use espresso to look for the fragment's text view and verify it is displayed
        onView(withId(R.id.textView)).check(matches(withText("I am a fragment")));
    }
}
```

The second thing to enable sharedTest is to create a directory called `sharedTest`, at the same directory level with `test` and `androidTest`. The Android Studio doesn't support it, so we should create it manually. [`FragmentScenarioSample`'s `sharedTest` directory][16] is a good example for it.

The next step we should do is to add `sharedTest` directory to `test`'s and `androidTest`'s source directory. [`FragmentScenarioSample`'s `build.gradle`][11] is also a good example for it:

```groovy
// share the unified tests
sourceSets {
    test {
        java.srcDir 'src/sharedTest/java'
    }
    androidTest {
        java.srcDir 'src/sharedTest/java'
    }
}
```

If you want to share resources too, you can check [`Robolectric`'s PR: Add ctesque common tests to android test][9] that used to reuse tests to improve CI fidelity with sharedTest pattern:

```groovy
sourceSets {
    String sharedTestDir = 'src/sharedTest/'
    String sharedTestSourceDir = sharedTestDir + 'java'
    String sharedTestResourceDir = sharedTestDir + 'resources'
    test.resources.srcDirs += sharedTestResourceDir
    test.java.srcDirs += sharedTestSourceDir
    androidTest.resources.srcDirs += sharedTestResourceDir
    androidTest.java.srcDirs += sharedTestSourceDir
}
```

The last thing is to test it with `./gradlew test` for local tests on `Robolectric` and `./gradlew connectedCheck` for instrumentation tests on Emulator.

## Why `AndroidJUnit4` test runner?

There is an aspirational long-term goal for Android tests, [write once, run everywhere tests on Android][1]. The [`AndroidJUnit4` test runner][3] is selected as the bridge for different devices that used to run tests. We can check [`AndroidJUnit4#getRunnerClassName()`][17], and we can find how [`AndroidJUnit4`][17] to delegate tests to real test runner based on running environment:

```java
private static String getRunnerClassName() {
  String runnerClassName = System.getProperty("android.junit.runner", null);
  if (runnerClassName == null) {
    if (!System.getProperty("java.runtime.name").toLowerCase().contains("android")
        && hasClass("org.robolectric.RobolectricTestRunner")) {
      return "org.robolectric.RobolectricTestRunner";
    } else {
      return "androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner";
    }
  }
  return runnerClassName;
}
```

If it finds current running environment has `RobolectricTestRunner`, it will delegate tests to `Robolectric`'s `RobolectricTestRunner`; otherwise to [`androidx.test`][13]'s `AndroidJUnit4ClassRunner`.

## Not only sharing code, but also speeding up development

With sharedTest pattern, we can share test code as much as possible. Is it the only benefit to encourage you to use sharedTest pattern? Not yet. Actually, `Robolectric` is a simulated Android environment inside a JVM. It has better speed to establish and destroy tests environment, and developers can get test result more quickly. It can help developers to speed up [TDD cycles](https://developer.android.com/training/testing/fundamentals#create-test-iteratively):

![The two cycles associated with iterative, test-driven development](https://developer.android.com/images/training/testing/testing-workflow.png)

## References

There are some articles have shown sharedTest pattern, and they are should be mentioned here:

[Sharing code between local and instrumentation tests by Alex Zhukovich][6]

[Powerful Approaches to Develop Shared Android Tests by Oleksandr Hrybuk][7]

[Sharing code between unit tests and instrumentation tests on Android by Dan Lew][8]

There is an awesome book has introduced sharedTest pattern too:

[Android Test-Driven Development by Tutorials, by Fernando Sproviero, Victoria Gonda and Lance Gleason, Razeware LLC (July 20, 2021)][12]

There are some Google's projects have used sharedTest pattern to sharing test code:

[accompanist: [All] Share tests to run on Robolectric & Emulators by chrisbanes][10]

[1]: https://medium.com/androiddevelopers/write-once-run-everywhere-tests-on-android-88adb2ba20c5 "Write Once, Run Everywhere Tests on Android"
[2]: http://robolectric.org/blog/2018/10/25/robolectric-4-0/ "Robolectric 4.0 release"
[3]: https://developer.android.com/training/testing/junit-runner "AndroidJUnit4 test runner"
[4]: https://developer.android.com/training/testing/espresso/ "Espresso"
[5]: https://developer.android.com/reference/androidx/test/core/app/ActivityScenario "ActivityScenario"
[6]: https://proandroiddev.com/sharing-code-between-local-and-instrumentation-tests-c0b57ebd3200 "Sharing code between local and instrumentation tests by Alex Zhukovich"
[7]: https://medium.com/wirex/powerful-approaches-to-develop-shared-android-tests-15c508e3ce8a "Powerful Approaches to Develop Shared Android Tests by Oleksandr Hrybuk"
[8]: https://blog.danlew.net/2015/11/02/sharing-code-between-unit-tests-and-instrumentation-tests-on-android/ "Sharing code between unit tests and instrumentation tests on Android by Dan Lew"
[9]: https://github.com/robolectric/robolectric/pull/6570 "Add ctesque common tests to android test"
[10]: https://github.com/google/accompanist/pull/180 "[All] Share tests to run on Robolectric & Emulators by chrisbanes"
[11]: https://github.com/android/testing-samples/blob/main/ui/espresso/FragmentScenarioSample/app/build.gradle "FragmentScenarioSample of androidx.test with sharedTest pattern"
[12]: https://www.raywenderlich.com/books/android-test-driven-development-by-tutorials/ "Android Test-Driven Development by Tutorials, by Fernando Sproviero, Victoria Gonda and Lance Gleason, Razeware LLC (July 20, 2021)"
[13]: https://github.com/android/android-test "androidx.test"
[14]: https://github.com/android/testing-samples/blob/main/ui/espresso/FragmentScenarioSample/app/src/sharedTest/java/com/example/android/testing/espresso/fragmentscenario/SampleFragmentTest.kt "SampleFragmentTest.kt of FragmentScenarioSample"
[15]: https://github.com/android/testing-samples/tree/main/ui/espresso/FragmentScenarioSample "FragmentScenarioSample of testing-samples"
[16]: https://github.com/android/testing-samples/tree/main/ui/espresso/FragmentScenarioSample/app/src/sharedTest "sharedTest directory of FragmentScenarioSample"
[17]: https://cs.android.com/androidx/android-test/+/master:ext/junit/java/androidx/test/ext/junit/runners/AndroidJUnit4.java "AndroidJUnit4 source code"