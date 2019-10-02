---
title: AndroidX Test
group: User Guide
order: 1
redirect_from: "/activity-lifecycle/"
---

# AndroidX Test

Robolectric is intended to be fully compatible with Android's official testing libraries since version 4.0.
As such we encourage you to try these new APIs and provide feedback. At some point the Robolectric equivalents will
be deprecated and removed. Using the AndroidX Test APIs reduces the cognitive load for you as a developer, with just one
set of APIs to learn for the same Android concept, no matter if you are writing an Robolectric test or an instrumentation
test. Furthermore it will make your tests more portable and compatible with our future plans.

### TestRunner

It is now possible to use the AndroidX test runner in Robolectric tests. If you require a custom test runner currently,
please check out the new [configuration and plugin API](http://robolectric.org/javadoc/4.2/org/robolectric/pluginapi/package-summary.html)
and let us know if there are any extension points missing that you require.

**Robolectric**
```kotlin
@RunWith(RobolectricTestRunner::class)
public class SandwichTest {
}
```

**AndroidX Test**
```kotlin
@RunWith(AndroidJUnit4::class)
public class SandwichTest {
}
```

### Application

Since most Android code is centric around the `Context`, getting hold of your application’s context is a typical task
for most tests. 

**Robolectric**
```kotlin
@Before fun setUp() {
  val app = RuntimeEnvironment.application as ExampleApplication
  app.setLocationProvider(mockLocationProvider)
}
```

This can be directly replaced with [`ApplicationProvider`](https://developer.android.com/reference/androidx/test/core/app/ApplicationProvider).
You may want to statically import it for readability. 

**AndroidX Test**
```kotlin
import androidx.test.core.app.ApplicationProvider.getApplicationContext

@Before fun setUp() {
  val app = getApplicationContext<LocationTrackerApplication>()
  app.setLocationProvider(mockLocationProvider)
}
```

## Activities

Robolectric provided `Robolectric.setupActivity()` for the coarse-grained usecase where you just require
a launched activity in the resumed state and ready and visible for the user to interact with. 

Robolectric also provided `Robolectric.buildActivity()` which returned an `ActivityController` that allowed
the developer to step through the Activity lifecycle. This has proved problematic as it requires developers
to fully understand valid lifecycle transitions and possible valid states. Using an `Activity` in an invalid state
has undefined behavior and can cause compatibility issues when running on different Android test runtimes
or when upgrading to newer versions of Robolectric.

[`ActivityScenario`](https://developer.android.com/reference/androidx/test/core/app/ActivityScenario) provides a
replacement for both of these usecases, but places tigher restrictions around lifecycle transitions, namely that
invalid or incomplete transitions are not possible. If you'd like a `Rule`-based equivalent please use
[`ActivityScenarioRule`](https://developer.android.com/reference/androidx/test/ext/junit/rules/ActivityScenarioRule)
instead.

**Robolectric**
```kotlin
import org.robolectric.Robolectric.buildActivity

class LocationTrackerActivityTest {
    @Test fun locationListenerShouldBeUnregisteredInCreatedState() {
        // GIVEN
        val controller = buildActivity<LocationTrackerActivity>().setup()

        // WHEN
        controller.pause().stop()

        // THEN
        assertThat(controller.get().locationListener).isNull()
     }
}

```

**Android X Test**
```kotlin
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch

class LocationTrackerActivityTest {
    @Test fun locationListenerShouldBeUnregisteredInCreatedState() {
        // GIVEN
        val scenario = launchActivity<LocationTrackerActivity>()

        // WHEN
        scenario.moveToState(Lifecycle.State.CREATED)

        // THEN
        scenario.onActivity { activity ->
          assertThat(activity.locationListener).isNull()
        }
     }
}

```

Note that in Robolectric since both the test and UI event loop ran on the same thread, synchronization was not an
issue but `ActivityScenario.onActivity` provides a safe way for accessing the activity, should you need to, that
will be guaranteed to be compatible with our future plans.

## Views

Robolectric has very limited APIs for `View` interaction; most often test writers could just use the Android SDK APIs such as
`Activity.findViewById()` which was safe since Robolectric tests do not have to worry about synchronization between test and
UI threads.

[Espresso](https://developer.android.com/training/testing/espresso/) is the view
matching and interaction library of choice for instrumentation tests. Since Robolectric
4.0, Espresso APIs are now supported in Robolectric tests.

```kotlin
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

## Fragments

Robolectric provides APIs such as `SupportFragmentUtil` and `SupportFragmentController` that offer coarse and fine
grained control of the Fragments lifecycle, which required developers to have a full understanding of lifecycle
transitions and valid final states, making it easy to shoot oneself in the foot for the same reasons as with Activities above.

AndroidX Test provides [`FragmentScenario`](https://developer.android.com/reference/androidx/fragment/app/testing/FragmentScenario),
which offers APIs to safely create your fragment under test and drive it through
valid transitions.

```kotlin
@RunWith(AndroidJUnit4::class)
class FragmentTest {
    @Test fun testEventFragment() {
        val fragmentArgs = Bundle()
        val factory = MyFragmentFactory()
        val scenario = launchFragmentInContainer<MyFragment>(fragmentArgs, factory)
        onView(withId(R.id.text)).check(matches(withText("Hello World!")))
    }
}

```

Read more aboout testing Fragments here: [developer.android.com/training/basics/fragments/testing](https://developer.android.com/training/basics/fragments/testing)

