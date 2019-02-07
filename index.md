---
# You don't need to edit this file, it's empty on purpose.
# Edit theme's home layout instead if you wanna make some changes
# See: https://jekyllrb.com/docs/themes/#overriding-theme-defaults
# layout: home
front: true
---

Running tests on an Android emulator or device is slow! Building, deploying, and launching the app often takes a minute or more. That's no way to do <abbr title="Test-Driven Development">TDD</abbr>. There must be a better way.

[Robolectric](http://robolectric.org/) is a framework that brings fast and reliable unit tests to Android. Tests run inside the JVM on your workstation in seconds. With Robolectric you can write tests like this:

```kotlin
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView

@RunWith(AndroidJUnit4::class)
class AddContactActivityTest {

    @Test fun inputTextShouldBeRetainedAfterActivityRecreation() {
        // GIVEN
        val contactName = "Test User"
        val scenario = launchActivity<AddContactActivity>()

        // WHEN
        // Enter contact name
        onView(withId(R.id.contact_name_text)).perform(typeText(contactName))
        // Destroy and recreate Activity
        scenario.recreate()

        // THEN
        // Check contact name was preserved.
        onView(withId(R.id.contact_name_text)).check(matches(withText(contactName)))
     }
}
```
[» Getting Started...](/getting-started/)

### Test APIs & Isolation

Unlike traditional emulator-based Android tests, Robolectric tests run inside a sandbox which allows allows the Android environment to be precisely configured to the desired conditions for each test, isolates each test from its neighbors, and extends the Android framework with test APIs which provide minute control over the Android framework's behavior and visibility of state for assertions.

While much of the Android framework will work as expected inside a Robolectric test, some Android components' regular behavior doesn't translate well to unit tests: hardware sensors need to be simulated, system services need to be loaded with test fixture data. In those cases, Robolectric provides a [test double](https://en.wikipedia.org/wiki/Test_double) that's suitable for most unit testing scenarios.

You can find documentation for Robolectric's test APIs by installing the [Robolectric plugin for Chrome](https://chrome.google.com/webstore/detail/pjepcinimnfnaoopahdkpkefnefdkdgh) and visiting the [Android API Reference](https://developer.android.com/reference/packages).

### Run Tests Outside of the Emulator

Robolectric lets you run your tests on your workstation, or on your continuous integration environment in a regular JVM, without an emulator. Because of this, the dexing, packaging, and installing-on-the emulator steps aren't necessary, reducing test cycles from minutes to seconds so you can iterate quickly and refactor your code with confidence.

### SDK, Resources, & Native Method Simulation

Robolectric handles inflation of views, resource loading, and lots of other stuff that's implemented in native C code on Android devices. This allows tests to do most things you could do on a real device. It's easy to provide your own implementation for specific SDK methods too, so you could simulate error conditions or real-world sensor behavior, for example.

### No Mocking Frameworks Required

An alternate approach to Robolectric is to use mock frameworks such as [Mockito](http://code.google.com/p/mockito/) to mock out the Android SDK. This is problematic because it relies on each individual developer to fully understand the implementation of Android which can change across API versions. Because Robolectric's test doubles are community mainained, well tested and in some cases verified against real Android they're a lot more accurate than one off mocks. Futhermore, because the implementation is encapsulated inside the test doubles, it removes the need for distracting boilerplate stubbing from your tests.

Robolectric allows a test style that is closer to black box testing, making the tests more effective for refactoring and allowing the tests to focus on the behavior of the application instead of the implementation of Android. We still recommend using a mocking framework along with Robolectric for your own dependencies.
