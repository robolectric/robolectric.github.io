---
title:  "Improving Robolectric's Looper simulation"
author: Brett Chabot and Jonathan Gerrish
---

**TL;DR: We'd love your feedback on improvements we've made to make Robolectric's Looper behavior 
more realistic.** Try it out today by annotating your tests with _@LooperMode(PAUSED)_ and let us 
know your experience!


## Background

Unlike on a real device, Robolectric shares a single thread for both UI operations and Test code. 
By default, Robolectric will execute tasks posted to Loopers synchronously inline. 
This causes Robolectric to execute tasks earlier than they would be on a real device. 
While in many cases this has no observable effect it can lead to bugs that are hard to track down.

Consider the code below. When run on the UI thread on a device, the assertion would pass.  


```
List<String> events = new ArrayList<>();
events.add("before");
new Handler(Looper.getMainLooper()).post(() -> events.add("after"));
events.add("between");

assertThat(events).containsExactly("before", "between", "after").inOrder();
```


Robolectric’s default behavior is to process posted code synchronously and immediately, so the
assertion fails with `[before, after, between]`, which is clearly incorrect.

Robolectric’ current implementation is notoriously prone to deadlocks, infinite loops and other race
 conditions. Robolectric will duplicate each task posted to a Looper into a separate list stored in 
 `org.roboelectric.util.Scheduler`. The Looper’s set of tasks and the Schedulers can get out of
 sync, causing hard-to-diagnose errors. 


## Solution

We’ve re-written Robolectric’s threading model and looper simulation in a way that we hope will
address the deficiencies of the current behavior. It’s available to try out now by applying a 
`@LooperMode(PAUSED)` annotation to your test classes or methods. Some of the highlights of the 
PAUSED mode vs the existing ‘LEGACY’ mode include:


*   Tasks posted to the main looper are not automatically executed inline. Similar to the
[ legacy paused IdleState](http://robolectric.org/javadoc/4.3/org/robolectric/util/Scheduler.IdleState.html#PAUSED), 
tasks posted to the main looper must be explicitly executed via `ShadowLooper` APIs. However, 
we’ve made a couple additional improvements in PAUSED mode that make this easier:
*   Robolectric will warn users if a test fails with unexecuted tasks in the main looper queue. 
This is a hint that the unexecuted behavior is important for the test case.
*   AndroidX Test APIs, like ActivityScenario, FragmentScenario and Espresso will automatically idle
 the main looper
*   Tasks posted to background loopers are executed in real threads. This will hopefully 
eliminate the need for hacks when trying to test code that asserts that it is running in a
background thread


## Using PAUSED LooperMode

To switch to PAUSED:
*   Use robolectric 4.3. In gradle add

    ```
    dependencies {
      testImplementation 'org.robolectric:robolectric:4.3'
    }
    ```
*   Apply the `LooperMode(PAUSED)` annotation to your test package/class/method
*   Convert any background `org.robolectric.util.Scheduler` calls for controlling `Loopers` to 
`shadowOf(looper)`
* Recommended, but not mandatory: Convert any foreground `org.robolectric.util.Scheduler` calls 
to `shadowOf(getMainLooper())`. The Scheduler APIs will be @deprecated and removed over time.
*    Convert any `org.robolectric.android.util.concurrent.RoboExecutorService` usages to 
`org.robolectric.android.util.concurrent.PausedExecutorService` or
`org.robolectric.android.util.concurrent.InlineExecutorService`
*   Run your tests. If you see test failures like _Main looper has queued unexecuted runnables_, 
you may need to insert `shadowOf(getMainLooper()).idle()` calls to your test to
drain the main Looper. Its recommended to step through your test code with a watch set on 
`Looper.getMainLooper().getQueue()` to see the status of the looper queue, to determine the
appropriate point to add a `shadowOf(getMainLooper()).idle()` call.

Example:

```
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
    // execute all tasks posted to main looper 
    shadowOf(getMainLooper()).idle();
   
    assertThat(events).containsExactly("before", "between", "after").inOrder();
  }
}
```


Take a look at Robolectric’s [ShadowPausedAsyncTaskTest](https://github.com/robolectric/robolectric/blob/master/robolectric/src/test/java/org/robolectric/shadows/ShadowPausedAsyncTaskTest.java) for an example of using PAUSED LooperMode and background tasks.


## Troubleshooting

*   Animations: Use of Animations can cause delayed tasks to be posted to the main looper queue. 
You can use the `ShadowLooper.idleFor` or `ShadowLooper.runToEndOfTasks` APIs to execute these tasks.

## Feedback

Please let us know of any roadblocks to adopting PAUSED LooperMode.
We’d like to make it the default mode for tests in the next release, and thus your feedback would be
 most welcome. 
