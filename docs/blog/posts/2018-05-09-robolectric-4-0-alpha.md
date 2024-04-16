---
date: 2018-05-09
authors:
  - jongerrish
  - brettchabot
  - xian
hide:
  - footer
slug: robolectric-4-0-alpha
---

# Robolectric 4.0 Alpha & Jetpack

The Robolectric team is super excited to announce the first alpha release of Robolectric 4.0, as well as some new developments in the world of Robolectric and Android testing in general.

<!-- more -->

### `androidx.test`
We’re collaborating closely with the Android Jetpack testing team to develop common APIs for writing Android tests that can run as both local (JVM-based) and on-device (instrumentation) tests. From 4.0 on, Robolectric will support Jetpack’s [androidx.test](https://developer.android.com/training/testing/) APIs, starting with support for the [`AndroidJUnit4` test runner](https://developer.android.com/training/testing/junit-runner), [`ActivityTestRule`](https://developer.android.com/training/testing/junit-rules), and [Espresso](https://developer.android.com/training/testing/espresso/) for interacting with UI components.

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

As you can see, many of the idioms common in instrumentation tests are now supported by Robolectric tests. In the near future we'll be expanding `androidx.test` support on Robolectric, as well as introducing some Robolectric-originated testing paradigms to traditional instrumentation tests.

### Binary Resources

In conjunction with Android Studio 3.2, Robolectric can now use resources processed using the Android build toolchain, and loads and handles those resources using the same logic as on an actual Android device. Robolectric's old idiosyncratic resource handling mode is still available for projects not yet using the latest version of the build toolchain.

To enable the use of toolchain-processed resources in Robolectric tests, make sure you're using Android Gradle Plugin version `com.android.tools.build:gradle:3.2.0-alpha14` or higher, and add the following to your `gradle.properties`:

```properties
android.enableUnitTestBinaryResources=true
```

### Release Notes

Release notes are [available here](https://github.com/robolectric/robolectric/releases/tag/robolectric-4.0-alpha-1). Robolectric 4.0 is currently in alpha release, meaning that it is not yet feature-complete, and APIs are likely to change before the final release. Use with caution.

---

As always, thanks for your pull requests, bug reports, ideas and questions! &#x1f4af;

_Your Robolectric maintainers,_
<br/>
[jongerrish@google.com](mailto:jongerrish@google.com), [brettchabot@google.com](mailto:brettchabot@google.com), and [christianw@google.com](mailto:christianw@google.com).