---
title: Automatic Refactorings
page_title: Automatic Refactorings
group: [hide]
---
## Background
Robolectric's APIs were very ad hoc for a while, so we're in the process of cleaning up a bunch of old, dubious decisions. Some of these changes require changes to test code. (ErrorProne)[https://errorprone.info/] offers the ability to suggest or perform refactorings on existing code.

We’re introducing a set of ErrorProne refactorings to help test authors keep current with changes to Robolectric’s APIs, and to recommend best practices in writing tests. We anticipate keeping these current with ongoing Robolectric releases.

## Refactorings

### Migrate uses of deprecated methods

| *before...*                               | *after...*                                            |
| ----------------------------------------- | ----------------------------------------------------- |
| `Application application =`<br>`    ShadowApplication.getApplicationContext();` | `Application application =`<br>`    RuntimeEnvironment.application` |

Where possible, we’ll include automated refactorings for API changes and deprecations.

### Call Android API methods on framework objects, not their shadows

| *before...*                               | *after...*                                            |
| ----------------------------------------- | ----------------------------------------------------- |
| `shadowOf(activity).finish();`            | `activity.finish();`                                  |
| `ShadowSystemClock.currentTimeMillis();`  | `SystemClock.currentTimeMillis();                     |

To reduce Robolectric’s API surface area, most `@Implementation` methods’ visibility is being changed from `public` to `protected`. (see [pull request](https://github.com/robolectric/robolectric/pull/3130) et al.). These methods should never have been called directly by test code anyway, so we’re switching them all to calls on the actual framework object.

### Don’t save shadow objects into variables or fields

| *before...*                               | *after...*                                            |
| ----------------------------------------- | ----------------------------------------------------- |
| `ShadowActivity shadowActivity =`<br>`    shadowOf(activityController.get());`<br>`shadowActivity.getResultCode();` | `Activity activity =`<br>`    activityController.get();`<br>`shadowOf(activity).getResultCode();` |

As we build out `androidx.test`, a goal is for Robolectric shadows to become much less user-visible, and perhaps eventually a mostly invisible implementation detail behind `androidx.test`’s test APIs. Toward that end, our judgement is that it makes sense to remove as many explicit references to specific shadow classes as possible from tests.
Additionally, our judgement is that keeping two very similar but subtly distinct references to the same “object” (e.g. itself and its shadow) is an antipattern and prone to causing confusion and bugs.
We therefore recommend that shadow instances never be saved or passed around, and only acquired at the site of its immediate use.Additional checks and refactorings
In the future, we’ll add ErrorProne checks for some common antipatterns in writing shadow classes.

Usage
TBD

Future stuff
* Candidates for additional refactorings and checks
* ShadowLooper.getShadowMainLooper() -> shadowOf(Looper.getMainLooper())
* static fields in Robolectric tests — bad idea.
