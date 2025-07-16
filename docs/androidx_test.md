# AndroidX Test

Robolectric is intended to be fully compatible with Android's official testing libraries since
[version 4.0][robolectric-4.0-release]. As such, we encourage you to try these new APIs and provide
feedback. At some point, the Robolectric equivalents will be deprecated and removed. Using the
AndroidX Test APIs reduces the cognitive load for you as a developer, with just one set of APIs to
learn for the same Android concept, no matter if you are writing an Robolectric test or an
instrumentation test. Furthermore, it will make your tests more portable and compatible with our
future plans.

## TestRunner

It is now possible to use the AndroidX test runner in Robolectric tests. If you require a custom
test runner, please check out the [configuration and plugin API][configuration-plugin-api], and let
us know if there are any extension points missing that you require.

!!! snippet "Robolectric"

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

!!! snippet "AndroidX Test"

    /// tab | Java
    ```java
    import androidx.test.ext.junit.runners.AndroidJUnit4;

    @RunWith(AndroidJUnit4.class)
    public class SandwichTest {
    }
    ```
    ///

    /// tab | Kotlin
    ```kotlin
    import androidx.test.ext.junit.runners.AndroidJUnit4

    @RunWith(AndroidJUnit4::class)
    class SandwichTest
    ```
    ///

## Application

Since most Android code is centric around a [`Context`][context-documentation], getting hold of your
application’s context is a typical task for most tests.

!!! snippet "Robolectric"

    /// tab | Java
    ```java
    import org.robolectric.RuntimeEnvironment;

    @Before
    void setUp() {
      ExampleApplication app = (ExampleApplication) RuntimeEnvironment.application;
      app.setLocationProvider(mockLocationProvider);
    }
    ```
    ///

    /// tab | Kotlin
    ```kotlin
    import org.robolectric.RuntimeEnvironment

    @Before
    fun setUp() {
      val app = RuntimeEnvironment.application as ExampleApplication
      app.setLocationProvider(mockLocationProvider)
    }
    ```
    ///

!!! snippet "AndroidX Test"

    /// tab | Java
    ```java
    import androidx.test.core.app.ApplicationProvider;

    @Before
    void setUp() {
      ExampleApplication app = ApplicationProvider.getApplicationContext<ExampleApplication>();
      app.setLocationProvider(mockLocationProvider);
    }
    ```
    ///

    /// tab | Kotlin
    ```kotlin
    import androidx.test.core.app.ApplicationProvider

    @Before
    fun setUp() {
      val app = ApplicationProvider.getApplicationContext<ExampleApplication>()
      app.setLocationProvider(mockLocationProvider)
    }
    ```
    ///

## Activities

Robolectric provides [`Robolectric.setupActivity()`][robolectric-setup-activity] for the
coarse-grained use case where you require a launched activity in the resumed state and visible for
the user to interact with.

Robolectric also provides [`Robolectric.buildActivity()`][robolectric-build-activity], which returns
an [`ActivityController`][activity-controller] that allows the developer to step through the
[`Activity`][activity-documentation] lifecycle. This has proved problematic as it requires
developers to fully understand valid lifecycle transitions and possible valid states. Using an
`Activity` in an invalid state has undefined behavior and can cause compatibility issues when
running on different Android test runtimes or when upgrading to newer versions of Robolectric.

[`ActivityScenario`][activity-scenario] provides a replacement for both of these use cases, but
places tighter restrictions around lifecycle transitions, namely that invalid or incomplete
transitions are not possible. If you'd like a [`Rule`][junit-rule]-based equivalent please use
[`ActivityScenarioRule`][activity-scenario-rule] instead.

!!! snippet "Robolectric"

    /// tab | Java
    ```java
    import org.robolectric.Robolectric;
    import org.robolectric.android.controller.ActivityController;

    public class LocationTrackerActivityTest {
        @Test
        public void locationListenerShouldBeUnregisteredInCreatedState() {
            // GIVEN
            ActivityController<LocationTrackerActivity> controller = Robolectric.buildActivity<LocationTrackerActivity>().setup();

            // WHEN
            controller.pause().stop();

            // THEN
            assertThat(controller.get().getLocationListener()).isNull();
         }
    }
    ```
    ///

    /// tab | Kotlin
    ```kotlin
    import org.robolectric.Robolectric

    class LocationTrackerActivityTest {
        @Test
        fun locationListenerShouldBeUnregisteredInCreatedState() {
            // GIVEN
            val controller = Robolectric.buildActivity<LocationTrackerActivity>().setup()

            // WHEN
            controller.pause().stop()

            // THEN
            assertThat(controller.get().locationListener).isNull()
         }
    }
    ```
    ///

!!! snippet "AndroidX Test"

    /// tab | Java
    ```java
    import androidx.lifecycle.Lifecycle;
    import androidx.test.core.app.ActivityScenario;

    public class LocationTrackerActivityTest {
        @Test
        public void locationListenerShouldBeUnregisteredInCreatedState() {
            // GIVEN
            ActivityScenario<LocationTrackerActivity> scenario = ActivityScenario.launchActivity<LocationTrackerActivity>();

            // WHEN
            scenario.moveToState(Lifecycle.State.CREATED);

            // THEN
            scenario.onActivity(activity -> assertThat(activity.getLocationListener()).isNull());
        }
    }
    ```
    ///

    /// tab | Kotlin
    ```kotlin
    import androidx.lifecycle.Lifecycle
    import androidx.test.core.app.ActivityScenario

    class LocationTrackerActivityTest {
        @Test
        fun locationListenerShouldBeUnregisteredInCreatedState() {
            // GIVEN
            val scenario = ActivityScenario.launchActivity<LocationTrackerActivity>()

            // WHEN
            scenario.moveToState(Lifecycle.State.CREATED)

            // THEN
            scenario.onActivity { activity ->
                assertThat(activity.locationListener).isNull()
            }
        }
    }
    ```
    ///

Note that in Robolectric since both the test and UI event loop run on the same thread,
synchronization is not an issue. [`ActivityScenario.onActivity`][activity-scenario-on-activity]
provides a safe way of accessing the `Activity`, should you need to, that will be guaranteed to be
compatible with our future plans.

## Views

Robolectric has very limited APIs for [`View`][view-documentation] interaction. In most cases, test
writers can just use Android APIs, such as [`Activity.findViewById()`][activity-find-view-by-id]
which was safe since Robolectric tests do not have to worry about synchronization between test and
UI threads.

[Espresso][espresso] is the `View` matching and interaction library of choice for instrumentation
tests. Since Robolectric 4.0, Espresso APIs are now supported in Robolectric tests.

/// tab | Java

```java
import static androidx.test.espresso.Espresso.onView;

@RunWith(AndroidJUnit4.class)
public class AddContactActivityTest {
    @Test
    public void inputTextShouldBeRetainedAfterActivityRecreation() {
        // GIVEN
        String contactName = "Test User";
        ActivityScenario<AddContactActivity> scenario = ActivityScenario.launchActivity<AddContactActivity>();

        // WHEN
        // Enter contact name
        onView(withId(R.id.contact_name_text)).perform(typeText(contactName));
        // Destroy and recreate Activity
        scenario.recreate();

        // THEN
        // Check contact name was preserved.
        onView(withId(R.id.contact_name_text)).check(matches(withText(contactName)));
     }
}
```

///

/// tab | Kotlin

```kotlin
import androidx.test.espresso.Espresso.onView

@RunWith(AndroidJUnit4::class)
class AddContactActivityTest {
    @Test
    fun inputTextShouldBeRetainedAfterActivityRecreation() {
        // GIVEN
        val contactName = "Test User"
        val scenario = ActivityScenario.launchActivity<AddContactActivity>()

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

///

## Fragments

AndroidX Test provides [`FragmentScenario`][fragment-scenario], which offers APIs to safely create
your [`Fragment`][fragment-documentation] under test and drive it through valid transitions.

/// tab | Java

```java
import androidx.fragment.app.testing.FragmentScenario;

@RunWith(AndroidJUnit4.class)
public class FragmentTest {
    @Test
    public void testEventFragment() {
        Bundle arguments = Bundle();
        MyFragmentFactory factory = MyFragmentFactory();
        FragmentScenario<MyFragment> scenario = FragmentScenario.launchFragmentInContainer<MyFragment>(arguments, factory);
        onView(withId(R.id.text)).check(matches(withText("Hello World!")));
    }
}
```

///

/// tab | Kotlin

```kotlin
import androidx.fragment.app.testing.FragmentScenario

@RunWith(AndroidJUnit4::class)
class FragmentTest {
    @Test
    fun testEventFragment() {
        val arguments = Bundle()
        val factory = MyFragmentFactory()
        val scenario = FragmentScenario.launchFragmentInContainer<MyFragment>(arguments, factory)
        onView(withId(R.id.text)).check(matches(withText("Hello World!")))
    }
}
```

///

Read more about testing Fragments [here][fragment-testing].

[activity-controller]: javadoc/latest/org/robolectric/android/controller/ActivityController.html
[activity-documentation]: https://developer.android.com/reference/android/app/Activity
[activity-find-view-by-id]: https://developer.android.com/reference/android/app/Activity#findViewById(int)
[activity-scenario]: https://developer.android.com/reference/androidx/test/core/app/ActivityScenario
[activity-scenario-on-activity]: https://developer.android.com/reference/androidx/test/core/app/ActivityScenario#onActivity(androidx.test.core.app.ActivityScenario.ActivityAction%3CA%3E)
[activity-scenario-rule]: https://developer.android.com/reference/androidx/test/ext/junit/rules/ActivityScenarioRule
[configuration-plugin-api]: https://github.com/robolectric/robolectric/releases/tag/robolectric-4.0
[context-documentation]: https://developer.android.com/reference/android/content/Context
[espresso]: https://developer.android.com/training/testing/espresso
[fragment-documentation]: https://developer.android.com/reference/androidx/fragment/app/Fragment
[fragment-scenario]: https://developer.android.com/reference/androidx/fragment/app/testing/FragmentScenario
[fragment-testing]: https://developer.android.com/training/basics/fragments/testing
[junit-rule]: https://junit.org/junit4/javadoc/latest/org/junit/Rule.html
[robolectric-4.0-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-4.0
[robolectric-build-activity]: javadoc/latest/org/robolectric/Robolectric.html#buildActivity
[robolectric-setup-activity]: javadoc/latest/org/robolectric/Robolectric.html#setupActivity(java.lang.Class)
[view-documentation]: https://developer.android.com/reference/android/view/View
