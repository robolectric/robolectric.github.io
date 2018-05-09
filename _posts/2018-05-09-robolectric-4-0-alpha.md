---
title:  "Robolectric 4.0 Alpha & Jetpack"
author: Brett Chabot, Jonathan Gerrish, and Christian Williams
---

The Robolectric team is super excited to announce the first alpha release of Robolectric 4.0, as well as some new developments in the world of Robolectric and Android testing in general.

### `androidx.test`
We’re collaborating closely with the Android testing team to develop common APIs for writing Android tests that works in both JVM-based and on-device tests. From 4.0 on, Robolectric will support Jetpack’s androidx.test APIs.

#### A Robolectric 3.x style test:
```kotlin
@RunWith(RobolectricTestRunner::class)
class RobolectricTest {
  @Test fun testTitle() {
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

  @Test fun testTitle() {
    onView(withId(R.id.button)).perform(click())
    intended(hasAction(equalTo("android.intent.action.EDIT")))
  }
}
```

### Binary Resources

In conjunction with Android Studio 3.2, Robolectric now uses resources processed using the Android build toolchain, and loads and handles those resources using the same logic as on an actual Android device.


As always, thanks for your pull requests, bug reports, ideas and questions! &#x1f4af;

_Your Robolectric maintainers,_
<br/>
[jongerrish@google.com](mailto:jongerrish@google.com), [brettchabot@google.com](mailto:brettchabot@google.com), and [christianw@google.com](mailto:christianw@google.com).