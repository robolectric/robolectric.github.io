# Best Practices & Limitations

## Best Practices

**DO** test layout inflation in your Robolectric test and ensure that click listeners are set up
correctly by testing the [`Activity`][activity-documentation] and layout interaction directly,
rather than mocking the [`LayoutInflater`][layout-inflater] or providing an abstraction over the
[`View`][view-documentation].

**DO** use public lifecycle APIs (e.g., via
[`Robolectric.buildActivity()`][robolectric-build-activity]), rather than exposing
`@VisibleForTesting` methods when testing Android components such as `Activity`s and
[`Service`][service-documentation]s. Calling those methods directly makes it difficult to refactor
the code under test later.

**DO** limit the number of threads that are running during each test. Rogue threads often cause test
pollution because they are not automatically cleaned up between tests. Oftentimes threads are
inadvertently spawned when using third-party libraries (e.g., for networking) or background
processing components. One of the main sources of additional threads during tests are
[`ExecutorService`][executor-service]s that maintain thread pools. If possible, mock dependent
components that spawn threads, or use
[`MoreExecutors.directExecutor()`][more-executors-direct-executor]. If it's necessary to run
multiple threads during a test, make sure to explicitly stop all threads and `ExecutorService`s to
avoid test pollution.

**DON'T** mock or spy on Android classes that will be acted on by other Android code (e.g.
[`Context`][context-documentation], [`SharedPreferences`][shared-preferences], and many others).
Stubbing is very brittle and can lead to breakages on Robolectric or Android Platform upgrades. The
small exceptions to this rule are classes with very narrow responsibilities, such as event
listeners.

## Limitations

Robolectric aims to accelerate Android unit testing by simulating the Android environment in the JVM, eliminating the need for emulators or physical devices.

However, it is important to be aware that Robolectric has certain limitations in replicating the complete Android environment. These limitations, as well as potential workarounds, are outlined in this document.

### Android libcore and OpenJDK have different implementations

Android libcore is a collection of Java libraries specifically designed for the Android platform. It provides essential functionalities for Android apps like networking, security, and file I/O. While libcore shares similarities with OpenJDK, it has its own implementation for some components, optimised for Android's resource constraints and security considerations.

Because of that, you may notice different behaviours when running your app on a device or emulator, and running your tests on the JVM with Robolectric.

Below is an incomplete list of differences you may encounter:

#### Timezone handling

When using [`SimpleDateFormat#parse()`][simple-date-format-parse] to parse a timezone using the `Z`
marker, Android supports time zones with a colon (i.e. `08:00`), while OpenJDK doesn't (i.e.
`0800`).

To work around this:

- If your min SDK version is 24 or higher, you can use the `X` marker instead.
- Use the `java.time` API, with [library desugaring][library-desugaring] enabled.
- Run the corresponding tests using regular Android tests.

**Related issues:** [#1030][robolectric-issue-1030], [#1257][robolectric-issue-1257],
[#5220][robolectric-issue-5220].

#### Month names

When using [`DateTimeFormatter.ofPattern(String)`][date-time-formatter-of-pattern] to create a
formatter displaying a short month name (using the `MMM` pattern), Android will use three-characters
names (i.e. `Jan`), while OpenJDK will use three-characters names followed by a period (i.e.
`Jan.`).

In this case, we recommend running the corresponding tests as regular Android tests.

**Related issues:** [#7910][robolectric-issue-7910].

[activity-documentation]: https://developer.android.com/reference/android/app/Activity
[context-documentation]: https://developer.android.com/reference/android/content/Context
[date-time-formatter-of-pattern]: https://developer.android.com/reference/kotlin/java/time/format/DateTimeFormatter#ofpattern
[executor-service]: https://developer.android.com/reference/kotlin/java/util/concurrent/ExecutorService
[layout-inflater]: https://developer.android.com/reference/android/view/LayoutInflater
[library-desugaring]: https://developer.android.com/studio/write/java8-support#library-desugaring
[more-executors-direct-executor]: https://guava.dev/releases/33.3.1-jre/api/docs/com/google/common/util/concurrent/MoreExecutors.html#directExecutor()
[robolectric-build-activity]: javadoc/latest/org/robolectric/Robolectric.html#buildActivity(java.lang.Class)
[robolectric-issue-1030]: https://github.com/robolectric/robolectric/issues/1030
[robolectric-issue-1257]: https://github.com/robolectric/robolectric/issues/1257
[robolectric-issue-5220]: https://github.com/robolectric/robolectric/issues/5220
[robolectric-issue-7910]: https://github.com/robolectric/robolectric/issues/7910
[service-documentation]: https://developer.android.com/reference/android/app/Service
[shared-preferences]: https://developer.android.com/reference/android/content/SharedPreferences
[simple-date-format-parse]: https://developer.android.com/reference/kotlin/java/text/SimpleDateFormat#parse
[view-documentation]: https://developer.android.com/reference/android/view/View
