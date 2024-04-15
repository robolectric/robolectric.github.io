---
title: Best Practices
group: User Guide
order: 8
---

# Best Practices

**DON'T** mock or spy on Android classes that will be acted on by other Android code (e.g. `Context`, `SharedPreferences`, and many others). Stubbing is very brittle and can lead to breakages on Robolectric or Android Platform upgrades. The small exceptions to this rule are classes with very narrow responsibilities, such as event listeners.

**DO** test layout inflation in your Robolectric test and ensure that click listeners are set up correctly by testing the `Activity` and `Layout` interaction directly rather than mocking the `LayoutInflater` or providing an abstraction over the view.

**DO** use public lifecycle APIs (e.g: via `Robolectric.buildActivity()`) rather than exposing `@VisibleForTesting` methods when testing Android components such as `Activities` and `Services`. Calling those methods directly makes it difficult to refactor the code under test later.

**DO** limit the number of the threads that are running during each test. Rogue threads often cause test pollution because they are not automatically cleaned up between tests. Oftentimes threads are inadvertently spawned when using third-party libraries (e.g for networking) or background processing components. One of the main sources of additional threads during tests are `ExecutorService`s that maintain thread pools. If possible, mock dependent components that spawn threads, or use a `DirectExecutor`. If it's necessary to run multiple threads during a test, make sure to explicitly stop all threads and `ExecutorService`s to avoid test pollution.
