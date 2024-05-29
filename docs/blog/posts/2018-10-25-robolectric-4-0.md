---
date: 2018-10-25
authors:
  - jongerrish
  - brettchabot
  - xian
slug: robolectric-4-0
---

# Robolectric 4.0 Released!

Robolectric 4.0 is released! Here's what's new!

### `androidx.test`
The different idioms for testing using Robolectric and instrumentation tests has long been a headache. With today's release of Robolectric 4.0 and `androidx.test` 1.0.0, both testing environments are converging on a set of common test APIs. Robolectric now supports the [`AndroidJUnit4` test runner](https://developer.android.com/training/testing/junit-runner), [`ActivityTestRule`](https://developer.android.com/training/testing/junit-rules), and [Espresso](https://developer.android.com/training/testing/espresso/) for interacting with UI components.

#### A Robolectric 3.x style test:
```kotlin
@RunWith(RobolectricTestRunner::class)
class RobolectricTest {
  @Test fun clickingOnTitle_shouldLaunchEditAction() {
    val activity = Robolectric.setupActivity(NoteListActivity::class.java)
    ShadowView.clickOn(activity.findViewById(R.id.title));
    assertThat(ShadowApplication.getInstance().peekNextStartedActivity().action)
            .isEqualTo("android.intent.action.EDIT")
  }
}
```

#### Robolectric 4.x/instrumentation test:
```kotlin
@RunWith(AndroidJUnit4::class)
class OnDeviceTest {
  @get:Rule val rule = ActivityTestRule(NoteListActivity::class.java)

  @Test fun clickingOnTitle_shouldLaunchEditAction() {
    onView(withId(R.id.button)).perform(click())
    intended(hasAction(equalTo("android.intent.action.EDIT")))
  }
}
```

As you can see, many of the idioms common in instrumentation tests are now supported by Robolectric tests. In future releases we'll be expanding `androidx.test` support on Robolectric, as well as introducing some Robolectric-originated testing paradigms to traditional instrumentation tests.

### Binary Resources

In conjunction with Android Studio 3.2, Robolectric can now use resources processed using the Android build toolchain, and loads and handles those resources using the same logic as on an actual Android device. Robolectric's old idiosyncratic resource handling mode is still available for projects not yet using the latest version of the build toolchain, but is now deprecated and will be removed in a future release.

To enable the use of toolchain-processed resources in Robolectric tests, make sure you're using [Android Gradle Plugin](https://developer.android.com/studio/releases/gradle-plugin#updating-plugin) version `com.android.tools.build:gradle:3.2.1` or higher, and add the following to your `gradle.properties`:

```groovy
android {
  testOptions {
    unitTests {
      includeAndroidResources = true
    }
  }
}

dependencies {
  testImplementation 'org.robolectric:robolectric:{{ robolectric.version.current_patched }}'
}
```

Add this line to your `gradle.properties` (no longer necessary with Android Studio 3.3+):

```properties
android.enableUnitTestBinaryResources=true
```

### Migration Tool

Robolectric 4.0 contains a number of API changes that may require modifications to your test code. Check out the [migration notes](../../migrating.md/#migrating-to-40) and use the new [automated migration tool](../../automated-migration.md) to help convert your existing tests to be compatible with Robolectric 4.0.

### Release Notes

Release notes are [available here](https://github.com/robolectric/robolectric/releases/tag/robolectric-4.0).

---

As always, thanks for your pull requests, bug reports, ideas and questions! &#x1f4af;

_Your Robolectric maintainers,_
<br/>
[jongerrish@google.com](mailto:jongerrish@google.com), [brettchabot@google.com](mailto:brettchabot@google.com), and [christianw@google.com](mailto:christianw@google.com).