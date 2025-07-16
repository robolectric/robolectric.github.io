---
hide:
- navigation
- toc
---

<!-- markdownlint-disable MD033 MD041 -->

<div align="center">
  <img src="images/robolectric-horizontal.png" alt="{{ config.site_name }}" />

  <h1>{{ config.site_description }}</h1>
</div>

Running tests on an Android emulator or device is slow! Building, deploying, and running the tests
often take minutes. That's no way to do <abbr title="Test-Driven Development">TDD</abbr>. There must
be a better way. Robolectric is a framework that brings fast and reliable unit tests to Android.
Tests run inside the JVM in seconds. With Robolectric you can write tests like this:

/// tab | Java

```java
--8<-- "snippets/java/src/test/java/org/robolectric/snippets/java/MyActivityTest.java:index_sample_test"
```

///

/// tab | Kotlin

```kotlin
@RunWith(AndroidJUnit4::class)
class MyActivityTest {
  @Test
  fun clickingButton_shouldChangeMessage() {
    Robolectric.buildActivity(MyActivity::class).use { controller ->
      controller.setup() // Moves the Activity to the RESUMED state
      val activity = controller.get()

      activity.findViewById(R.id.button).performClick()
      assertEquals(activity.findViewById<TextView>(R.id.text).text, "Robolectric Rocks!")
    }
  }
}
```

///

[Get started](getting-started.md){ .md-button .md-button--primary }

<div class="grid cards" markdown>

- **Test APIs & Isolation**

    ---

    Unlike traditional emulator-based Android tests, Robolectric tests run inside a sandbox which
    allows the Android environment to be precisely configured to the desired conditions for each
    test, isolates each test from its neighbors, and extends the Android framework with test APIs
    which provide minute control over the Android framework's behavior and visibility of state for
    assertions.

    While much of the Android framework will work as expected inside a Robolectric test, some
    Android components' regular behavior doesn't translate well to unit tests: hardware sensors need
    to be simulated, system services need to be loaded with test fixture data. In those cases,
    Robolectric provides a [test double][test-double] that's suitable for most unit testing
    scenarios.

- **No Mocking Frameworks Required**

    ---

    An alternate approach to Robolectric is to use mock frameworks such as [Mockito][mockito] or to
    mock out the Android SDK. While this is a valid approach, it often yields tests that are
    essentially reverse implementations of the application code.

    Robolectric allows a test style that is closer to black box testing, making the tests more
    effective for refactoring and allowing the tests to focus on the behavior of the application
    instead of the implementation of Android. You can still use a mocking framework along with
    Robolectric if you like.

- **Run Tests Outside the Emulator**

    ---

    Robolectric lets you run your tests on your workstation, or on your continuous integration
    environment, in a regular JVM, without an emulator. Because of this, the dexing, packaging, and
    installing steps aren't necessary, reducing test cycles from minutes to seconds so you can
    iterate quickly and refactor your code with confidence.

- **SDK, Resources, & Native Method Simulation**

    ---

    Robolectric handles inflation of `View`s, resource loading, and lots of other stuff that's
    implemented in native C code on Android devices. This allows tests to do most things you could
    do on a real device. You can provide your own implementation for specific SDK methods too, so
    you could simulate error conditions or real-world sensor behavior, for example.

</div>

[mockito]: https://site.mockito.org/
[test-double]: https://en.wikipedia.org/wiki/Test_double
