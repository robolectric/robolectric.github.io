---
date: 2019-06-04
authors:
  - brettchabot
  - jongerrish
slug: paused-looper
---

# Improving Robolectric's Looper simulation

**TL;DR: We'd love your feedback on improvements we've made to make Robolectric's [`Looper`][looper]
behavior more realistic.**
Try it out today by annotating your tests with [`@LooperMode(PAUSED)`][looper-mode] and let us know
your experience!

## Background

Unlike on a real device, Robolectric shares a single thread for both UI operations and Test code. 
By default, Robolectric will execute tasks posted to `Looper`s synchronously inline. 
This causes Robolectric to execute tasks earlier than they would be on a real device. 
While in many cases this has no observable effect, it can lead to bugs that are hard to track down.

Consider the code below. When run on the UI thread on a device, the assertion would pass.

```java
List<String> events = new ArrayList<>();
events.add("before");
new Handler(Looper.getMainLooper()).post(() -> events.add("after"));
events.add("between");

assertThat(events).containsExactly("before", "between", "after").inOrder();
```

Robolectric’s default behavior is to process posted code synchronously and immediately, so the
assertion fails with `[before, after, between]`, which is clearly incorrect.

Robolectric’s current implementation is notoriously prone to deadlocks, infinite loops and other
race conditions. Robolectric will duplicate each task posted to a `Looper` into a separate list
stored in [`Scheduler`][scheduler]. The `Looper`’s set of tasks and the `Scheduler`s can get out of
sync, causing hard-to-diagnose errors.

## Solution

We’ve re-written Robolectric’s threading model and `Looper` simulation in a way that we hope will
address the deficiencies of the current behavior. It’s available to try out now by applying a
`@LooperMode(PAUSED)` annotation to your test classes or methods. Some of the highlights of the
[`PAUSED`][looper-mode-paused] mode vs. the existing [`LEGACY`][looper-mode-legacy] mode include:

* Tasks posted to the main `Looper` are not automatically executed inline. Similar to the
  [legacy `PAUSED` `IdleState`][idle-state-paused], tasks posted to the main `Looper` must be
  explicitly executed via [`ShadowLooper`][shadow-looper] APIs. However, we’ve made a couple
  additional improvements in `PAUSED` mode that make this easier:
* Robolectric will warn users if a test fails with unexecuted tasks in the main `Looper` queue.
  This is a hint that the unexecuted behavior is important for the test case.
* AndroidX Test APIs, like [`ActivityScenario`][activity-scenario],
  [`FragmentScenario`][fragment-scenario] and [Espresso][espresso] will automatically idle the main
  `Looper`.
* Tasks posted to background `Looper`s are executed in real threads. This will hopefully
  eliminate the need for hacks when trying to test code that asserts that it is running in a
  background thread.

## Using `PAUSED` `LooperMode`

To switch to `PAUSED`:

* Use Robolectric 4.3. In Gradle, add:

    ```groovy
    dependencies {
      testImplementation 'org.robolectric:robolectric:4.3'
    }
    ```

* Apply the `@LooperMode(PAUSED)` annotation to your test package/class/method.
* Convert any background `Scheduler` calls for controlling `Looper`s to
  [`shadowOf(looper)`][shadow-of-looper].
* Recommended, but not mandatory: Convert any foreground `Scheduler` calls to
`shadowOf(getMainLooper())`. The `Scheduler` APIs will be deprecated and removed over time.
* Convert any [`RoboExecutorService`][robo-executor-service] usages to
  [`PausedExecutorService`][paused-executor-service] or
  [`InlineExecutorService`][inline-executor-service].
* Run your tests. If you see test failures like `Main looper has queued unexecuted runnables`, 
you may need to insert `shadowOf(getMainLooper()).idle()` calls to your test to drain the main
`Looper`. It's recommended to step through your test code with a watch set on
`Looper.getMainLooper().getQueue()` to see the status of the `Looper` queue, to determine the
appropriate point to add a `shadowOf(getMainLooper()).idle()` call.

Example:

```java
import static android.os.Looper.getMainLooper;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;
import static org.robolectric.Shadows.shadowOf;

import org.robolectric.annotation.LooperMode;

@RunWith(AndroidJUnit4.class)
@LooperMode(PAUSED)
public class MyTest {
  @Test
  public void testCodeThatPosts() {
    List<String> events = new ArrayList<>();
    events.add("before");
    new Handler(Looper.getMainLooper()).post(() -> events.add("after"));
    events.add("between");

    // the 'after' task is posted, but has not been executed yet
    assertThat(events).containsExactly("before", "between").inOrder();
    // execute all tasks posted to main Looper 
    shadowOf(getMainLooper()).idle();
   
    assertThat(events).containsExactly("before", "between", "after").inOrder();
  }
}
```

Take a look at Robolectric’s [`ShadowPausedAsyncTaskTest`][shadow-paused-async-task-test] for an
example of using `PAUSED` `LooperMode` and background tasks.

## Troubleshooting

* Animations: Use of Animations can cause delayed tasks to be posted to the main `Looper` queue. You
  can use the [`ShadowLooper.idleFor()`][shadow-looper-idle-for] or
  [`ShadowLooper.runToEndOfTasks()`][shadow-looper-run-to-end-of-tasks] APIs to execute these tasks.

## Feedback

Please let us know of any roadblocks to adopting `PAUSED` `LooperMode`.
We’d like to make it the default mode for tests in the next release, and thus your feedback would be
most welcome.

[activity-scenario]: https://developer.android.com/reference/androidx/test/core/app/ActivityScenario
[espresso]: https://developer.android.com/training/testing/espresso
[fragment-scenario]: https://developer.android.com/reference/androidx/fragment/app/testing/FragmentScenario
[idle-state-paused]: ../../javadoc/latest/org/robolectric/util/Scheduler.IdleState.html#PAUSED
[inline-executor-service]: ../../javadoc/latest/org/robolectric/android/util/concurrent/InlineExecutorService.html
[looper]: https://developer.android.com/reference/android/os/Looper.html
[looper-mode]: ../../javadoc/latest/org/robolectric/annotation/LooperMode.html
[looper-mode-legacy]: ../../javadoc/latest/org/robolectric/annotation/LooperMode.Mode.html#LEGACY
[looper-mode-paused]: ../../javadoc/latest/org/robolectric/annotation/LooperMode.Mode.html#PAUSED
[paused-executor-service]: ../../javadoc/latest/org/robolectric/android/util/concurrent/PausedExecutorService.html
[robo-executor-service]: ../../javadoc/latest/org/robolectric/android/util/concurrent/RoboExecutorService.html
[scheduler]: ../../javadoc/latest/org/robolectric/util/Scheduler.html
[shadow-looper]: ../../javadoc/latest/org/robolectric/shadows/ShadowLooper.html
[shadow-looper-idle-for]: ../../javadoc/latest/org/robolectric/shadows/ShadowLooper.html#idleFor(long,java.util.concurrent.TimeUnit)
[shadow-looper-run-to-end-of-tasks]: ../../javadoc/latest/org/robolectric/shadows/ShadowLooper.html#runToEndOfTasks()
[shadow-of-looper]: ../../javadoc/latest/org/robolectric/Shadows.html#shadowOf(android.os.Looper)
[shadow-paused-async-task-test]: https://github.com/robolectric/robolectric/blob/master/robolectric/src/test/java/org/robolectric/shadows/ShadowPausedAsyncTaskTest.java
