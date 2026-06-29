---
date: 2026-06-14
authors:
  - utzcoz
slug: designing-local-testing-libraries
---

# Designing Robolectric-based local-testing libraries for platforms and modules

A *local-testing library* is a small companion artifact (in AndroidX, often a sibling module named
`<module>-testing`) that lets developers exercise some Android surface in plain JVM unit tests, with
no emulator or device. That surface is one of two kinds:

- an **AOSP-based or customized platform**: a whole Android variant, such as Android TV or Android
  Automotive, with its own system services and APIs;
- a **module, library, or SDK**: anything shipped as a library that exposes APIs developers build on,
  whether an expanded platform feature such as Android XR or AppFunctions, or an app-facing component
  such as Compose UI, Glance, WorkManager, or Navigation.

Either way the goal is the same: let the people coding against it test their code quickly and
deterministically, off-device. Many AndroidX libraries ship exactly such an artifact, distributed as
an ordinary dependency that developers add to their own library or app's test configuration, and the
engine underneath is almost always Robolectric. It is what lets a Compose UI test, an Android XR
perception test, or a WorkManager worker run on the JVM in milliseconds instead of on an emulator.
This post dissects eleven of them to show how they use Robolectric, why it fits the job, and why a
similar approach is worth adopting if you ship a custom AOSP-expanded feature or a customized AOSP
platform.

This is an outside read of the code as it stands in mid-2026, not an official AndroidX position;
specifics will shift as the libraries evolve. Every claim is grounded in real code in the AndroidX
(`frameworks/support`) tree, linked inline.

## Why a local-testing library, and why Robolectric

Android sorts tests by where they run: *local* tests on your machine's JVM, *instrumented* tests on
a device or emulator ([fundamentals][fundamentals]). Local tests are fast and hermetic; the guidance
is to reserve device tests for "cases where you must test against the behavior of a real device." A
platform with no local-testing story pushes every consumer onto the slow, flaky end of that
tradeoff.

Android's guidance also prefers *fakes* (lightweight, no mocking framework) over mocks, and tells
consumers to "check with the authors to see if they provide any officially-supported testing
infrastructures, such as fakes, that you can reliably depend on" ([test doubles][test-doubles]). If
you own a platform or module, you are those authors.

Robolectric is what makes a fakes-based library practical, and the mechanism is simple to state: it
gives your tests a fake Android environment on the JVM. Instead of an emulator it loads the real
Android framework classes and replaces their device-and-native behavior with stand-ins, so
`Context`, resources, `Looper`, `Parcel`, SQLite, binders, and even graphics behave much like they do on a
real device, just in-process and fast ([architecture][architecture]). You choose the SDK level with
`@Config` and can switch on real graphics for screenshots ([configuring][configuring]). That is the
whole idea: a real-enough Android runtime without a device.

The piece that ties it together is the `androidx.test.ext.junit.runners.AndroidJUnit4` runner: the
*same* `@RunWith(AndroidJUnit4::class)` test runs on Robolectric locally and on a real device when
instrumented ([Robolectric strategies][strategies]). So a good test library never chooses
"Robolectric or device". It targets that unified runner and runs in both. Robolectric is best
thought of as a deployment target for your tests, not a separate code path.

## How eleven AndroidX libraries do it

The eleven libraries fall into five families. Each one fakes the smallest seam it can and lets
Robolectric carry the surrounding Android runtime.

### Family A — One test, two runtimes

The ideal: write a test once, run it on the JVM for speed and on a device for fidelity, unchanged.
Compose is the most ambitious example.

#### A1. Jetpack Compose UI test (`compose/ui/ui-test`, `ui-test-junit4`)

Compose normally renders through a real `Choreographer`/`RenderThread`/VSYNC loop. The same
`createAndroidComposeRule<ComponentActivity>()` / `runComposeUiTest { }` code runs on a device and
on the JVM from one binary, with no `robolectricMain` source set. The only runtime detection is a
single fingerprint check ([`RobolectricIdlingStrategy.android.kt`][idling]):

```kotlin
internal val HasRobolectricFingerprint
    get() = Build.FINGERPRINT.lowercase() == "robolectric"
```

That boolean swaps one internal `IdlingStrategy` (Espresso on device; a main-thread loop that pumps
Compose and forces layout on the JVM, where there is no draw pass). Frames come from a virtual-time
test clock rather than a real `Choreographer`, and screenshot tests use Robolectric's
native-graphics `PixelCopy` shadow. The tests live in an `androidHostTest` (JVM/Robolectric) source
set beside `androidDeviceTest` (instrumented), sharing one body.

### Family B — Contract + fake (the test-double triad)

These libraries define a contract interface, ship a real implementation and a fake, and hand the
fake to consumers through a rule or factory.

#### B1. Android XR (`xr/runtime`, `scenecore`, `arcore`, `projected`)

XR code runs against a runtime that only exists on XR hardware. XR layers its public API (`Session`,
`Anchor`, `Entity`) over `@RestrictTo` runtime interfaces (`JxrRuntime`, `PerceptionManager`, …)
that either a device implementation or a fake can satisfy; each `-testing` module ships in-memory
`Fake*` implementations declaring `requirements = emptySet()`. `Session.create()` discovers
factories via a provider list plus `ServiceLoader` and picks the first whose feature requirements
the device meets. Under Robolectric, the device reports no features, so only the zero-requirement
fakes are eligible ([`ServiceLoaderExt.kt`][serviceloader]):

```kotlin
internal fun getDeviceContextFeatures(context: Context): Set<Feature> {
    if (Build.FINGERPRINT.contains("robolectric")) return emptySet()  // short-circuit for unit tests
    ...
}
```

Developers touch a `TestRule` (`ArCoreTestRule`, `SceneCoreTestRule`) that reaches the
reflectively-built fake through a static-field handshake. The fakes contain no Robolectric; it only
supplies the JVM `Context`/`Activity` and the fingerprint. The exception is `projected-testing`,
whose contract *is* the OS (virtual devices, AIDL), so it shadows the platform and mocks binders
instead of hand-writing a fake.

#### B2. AppFunctions (`appfunctions/appfunctions-testing`)

An AppFunction is normally invoked through the `AppFunctionManager` system service. The module
exposes one public type whose `getAppFunctionManager()` returns the *real* production manager with
two fake internal backends injected ([`AppFunctionTestRule.kt`][appfunctiontestrule]):

```kotlin
public class AppFunctionTestRule(private val context: Context) : TestRule {
    private val appFunctionReader = FakeAppFunctionReader(context)
    private val appFunctionManagerApi = FakeAppFunctionManagerApi(context, appFunctionReader)
    // ...apply() also sets one ShadowSystemProperties override...
    public fun getAppFunctionManager(): AppFunctionManager =
        AppFunctionManager(context, appFunctionReader, appFunctionManagerApi, NullTranslatorSelector())
}
```

Tests run the genuine public API against fakes of its internal interfaces; the fake reader seeds a
`MutableStateFlow` from the KSP-generated inventory, and execution runs the real delegate
in-process. Robolectric's only runtime job is one `ShadowSystemProperties.override(...)` for a
missing SDK-extension property.

#### B3. Navigation (`navigation/navigation-testing`)

A `NavController` only exists hosted in an Activity/Fragment with a `Context`, lifecycle, and real
navigators. `TestNavHostController` *extends* the real controller and swaps only the navigator
registry ([`TestNavHostController.android.kt`][testnavhost]):

```kotlin
public actual class TestNavHostController(context: Context) :
    NavHostController(context.applicationContext) {
    public actual val backStack: List<NavBackStackEntry> get() = currentBackStack.value
    init { navigatorProvider = TestNavigatorProvider() }
}
```

Its `setCurrentDestination` is built on the production deep-link machinery, not private state, so
the back stack it produces is the real one. The fakes are hand-written and multiplatform;
Robolectric runs only the Android-host tests that need real `Bundle`/`Uri` behavior.

#### B4. Lifecycle ViewModel (`lifecycle/lifecycle-viewmodel-testing`)

A `ViewModel` needs a `ViewModelStoreOwner` + `SavedStateRegistryOwner` + lifecycle from its host,
and the hardest thing to test is process death. `viewModelScenario { }` fabricates that owner trio
in-memory and exposes `recreate()` ([`ViewModelScenarioOwner.kt`][viewmodelowner]):

```kotlin
viewModelScenario { TestViewModel(handle = createSavedStateHandle()) }.use { scenario ->
    scenario.viewModel.handle["key"] = "value"
    scenario.recreate()                                  // simulate process death
    assertThat(scenario.viewModel.handle.get<String>("key")).isEqualTo("value")
}
```

`recreate()` round-trips the saved state through a real `Parcel` (and enforces the 1 MB Binder
limit), reproducing real serialization loss rather than an in-memory copy. Robolectric supplies that
JVM `Parcel`/`Bundle`, attached on the Android host through a thin `expect`/`actual` test base.

### Family C — Production-owned seams

Here the production library was built with test substitution in mind. It exposes a seam the official
test artifact can reach but ordinary apps cannot.

#### C1. Window (`window/window-testing`)

`androidx.window` surfaces fold state, activity embedding, and window metrics that exist only on
specific hardware or OEM builds. Every production factory routes through a swappable `decorator`
field (default identity) behind `@RestrictTo(LIBRARY_GROUP)` `overrideDecorator()`/`reset()`. Each
test rule installs a decorator and resets it in `finally`
([`WindowLayoutInfoPublisherRule.kt`][windowrule]):

```kotlin
public class WindowLayoutInfoPublisherRule : TestRule {
    private val flow = MutableSharedFlow<WindowLayoutInfo>(
        extraBufferCapacity = 1, onBufferOverflow = DROP_OLDEST)
    override fun apply(base: Statement, description: Description) = object : Statement() {
        override fun evaluate() {
            WindowInfoTracker.overrideDecorator(PublishWindowInfoTrackerDecorator(flow))
            try { base.evaluate() } finally { WindowInfoTracker.reset() }
        }
    }
    public fun overrideWindowLayoutInfo(info: WindowLayoutInfo) { flow.tryEmit(info) }
}
```

The fakes are pure JUnit; Robolectric only provides the JVM Android types (`Rect`,
`WindowLayoutInfo`) so the tests can build them off-device.

#### C2. WorkManager (`work/work-testing`)

WorkManager runs deferrable work on the OS scheduler and clock. `work-testing` keeps the *real*
engine and a real in-memory Room database, faking only the trigger seam and the executor. It
installs the real engine via a static delegate with a `SynchronousExecutor`, and `TestDriver`
exposes imperative levers, so a test sees the finished state immediately
([`TestScheduler.kt`][testscheduler]):

```java
workManager.enqueue(request);
assertThat(workManager.getWorkInfoById(id).get().getState().isFinished(), is(false));
mTestDriver.setAllConstraintsMet(id);                                  // the lever
assertThat(workManager.getWorkInfoById(id).get().getState().isFinished(), is(true));  // ran inline
```

Here Robolectric does the heavy lifting: it provides the `Context`, SQLite, and `Looper` so the
entire real engine runs on the JVM.

### Family D — Host and service harnesses

These components are driven by a remote host or a bound service that does not exist on a JVM. The
test library plays that host itself, in-process.

#### D1. Car App Library (`car/app/app-testing`)

A Car App is bound by a host (head unit) over AIDL binders that don't exist on a JVM.
`TestCarContext.createCarContext()` wires a `FakeHost` and recording managers, and `getCarService`
returns the doubles. The trick is faking the *callback* direction so output is assertable
([`TestCarContext.java`][testcarcontext]):

```java
class TestAppHost extends IAppHost.Stub {
    @Override public void invalidate() {
        Screen top = mTestCarContext.getCarService(TestScreenManager.class).getTop();
        mTestCarContext.getCarService(TestAppManager.class)
            .addTemplateReturned(top, top.onGetTemplate());   // captured for assertions
    }
}
```

`SessionController`/`ScreenController` drive the lifecycle via `moveToState(...)`. Robolectric supplies
the JVM `Context`/`Intent` and one broadcast shadow.

#### D2. Wear Tiles (`wear/tiles/tiles-testing`)

A Tile is delivered by a bound `TileService` over a proto AIDL interface. Rather than mock the
client, `TestTileClient` runs the *real* `DefaultTileClient` and fakes only the bind, using
Robolectric's `ServiceController` + `ShadowApplication` ([`TestTileClient.kt`][testtileclient]):

```kotlin
private fun maybeBind() {
    if (!hasBound) {
        val binder = controller.create().get().onBind(controller.intent)
        shadowOf(getApplicationContext<Application>())
            .setComponentNameAndServiceForBindServiceForIntent(
                controller.intent, componentName, binder)
    }
}
```

The full proto round-trip runs unchanged, so real serialization is tested. A custom
`RobolectricTestRunner` with `doNotInstrumentPackage(...)` keeps the binder/proto packages running
as real bytecode.

### Family E — Assertion DSLs over data trees

When a platform's output is a serializable tree rather than pixels, the test library can skip device
emulation almost entirely and assert on the structure directly.

#### E1. Glance (`glance/glance-testing`, `glance-appwidget-testing`)

Glance widgets are a Compose composition that emits a tree of `Emittable`s. Modeled on
`runComposeUiTest`, the harness runs a real composition with no rendering and asserts on the tree
([`GlanceAppWidgetUnitTest.kt`][glanceunit]):

```kotlin
fun header_smallSize_showsShortText() = runGlanceAppWidgetUnitTest {
    setAppWidgetSize(DpSize(50.dp, 100.dp))
    provideComposable { StatusRow(status = false) }
    onNode(hasTestTag("header-text")).assert(hasText("MyApp"))
}
```

The environment is faked entirely through Glance composition locals (`LocalSize`, `LocalState`, …);
the library contains no Robolectric, and a consumer only needs it when a composable reads resources.
The matcher and assertion engine is its own surface-agnostic layer with composable, self-describing
matchers.

#### E2. Wear ProtoLayout (`wear/protolayout/protolayout-testing`)

ProtoLayout describes UI as a serializable tree, some values computed by dynamic expressions. Almost
nothing is faked: the tree is real (round-tripped through proto) and only the dynamic-data inputs
are injected ([`LayoutElementAssertionsProvider.kt`][protolayout]):

```kotlin
val provider = LayoutElementAssertionsProvider(TEST_LAYOUT)
    .withDynamicData(intAppKey mapTo intAppValue,
                     PlatformHealthSources.Keys.DAILY_STEPS mapTo dailySteps)
provider.onElement(isText).assert(hasText("12,345 steps"))
```

Dynamic values resolve through the *real* evaluator, so you test the actual pipeline. This module
barely touches Robolectric: its tests use `@RunWith(AndroidJUnit4::class)` and a few `android.*`
value classes, nothing more. It is the counter-example showing how little Robolectric you sometimes
need.

## What Robolectric actually provides

Across all eleven cases, Robolectric plays the same role. It is almost never the code under test,
and almost never the test double itself; the faking is done by hand-written fakes, rules, and
harnesses. What Robolectric provides is the ground they stand on: a real Android runtime on the JVM.
It loads the actual framework classes rather than the throwaway `Stub!` ones, so `Context`,
resources, `Looper`, `Parcel`, SQLite, binders, and graphics behave closely enough to a device that
production code runs unmodified, in-process. Its role ranges widely across the libraries.

| Library | What Robolectric does for it |
| --- | --- |
| WorkManager | Runs the *entire* real engine and its Room/SQLite database on the JVM |
| Compose UI | JVM `Looper` plus native-graphics shadows for rendering and `PixelCopy` screenshots |
| Wear Tiles | `ServiceController` + `ShadowApplication` to bind a real `TileService` with no IPC |
| Car App | JVM `Context`/`Intent`/`PendingIntent`, plus one broadcast shadow, for the fake host |
| XR `projected` | Shadows `VirtualDeviceManager`/`PackageManager`; AIDL binders are mocked on top |
| AppFunctions | One `ShadowSystemProperties` override for a missing SDK-extension property |
| XR arcore/scenecore | Just the JVM `Context`/`Activity` and the `robolectric` fingerprint; fakes do the rest |
| Window / Navigation / Lifecycle | JVM Android primitives (`Rect`, `Bundle`, `Parcel`, `Looper`) so real types build off-device |
| Glance / ProtoLayout | Little to nothing: a real `Context` only when a composable reads resources |

The table shows the range: one dependency stretches from running WorkManager's entire engine and
database to lending Glance a single `Context`. That range is worth building on for three reasons.
The tests are fast and deterministic, finishing in milliseconds with no emulator boot and none of a
device's flakiness, so they sit in the run-on-every-commit tier of the pyramid. They are
high-fidelity, because they run the real framework code instead of a guessed stub, so the surface
developers build on behaves like the real thing. And they do not lock anyone in: the same
`@RunWith(AndroidJUnit4::class)` test still runs on a device when instrumented, so leaning on
Robolectric never forecloses real-device testing.

It is not a full emulator, and that is the point. Robolectric handles the platform plumbing while
your library supplies the behavior specific to it; anything that truly needs real hardware or
cross-process IPC still belongs in an instrumented test. Within those limits, that pairing of broad
reach and one uniform, device-compatible execution model is exactly what makes it the right
foundation for a local-testing library.

## A checklist for your own platform

Distilled from the eleven libraries, this is the recipe for shipping your own. Each step points to a
case above that shows it in practice.

1. **Find the seam.** Pin down the one boundary that must vanish in a test: a system service
   (AppFunctions), a runtime or factory (XR), a host binder (Car, Tiles), an OS trigger and clock
   (WorkManager), a hardware signal (Window), or a host owner (Navigation, Lifecycle). Fake that, and
   nothing more.
2. **Pick a substitution mechanism.** If the entry point is injectable, just pass a fake (Lifecycle,
   Glance). If it is built reflectively, register the fake through `ServiceLoader` and feature-gate it
   so it wins under Robolectric (XR). If it is a global singleton, add a `@RestrictTo` override hook
   (Window) or a static delegate (WorkManager).
3. **Prefer a fake to a shadow.** Write a plain object that implements your real interface and holds
   in-memory state. Reach for a Robolectric shadow only for genuine platform surfaces (a system
   property in AppFunctions, service binding in Tiles, a `Parcel` in Lifecycle), and shadow the OS
   outright only when the contract *is* the OS (XR `projected`).
4. **Pick a public shape.** A JUnit `TestRule` (XR, Window, Car), a scenario object (Lifecycle), or a
   `run…Test { }` harness (Compose, Glance). Keep the fakes themselves `internal`.
5. **Make it deterministic.** Collapse async with synchronous executors, drive frames with a
   virtual-time clock instead of `Choreographer`, and expose an explicit idle/await primitive.
6. **Target `AndroidJUnit4`.** One test body then runs on Robolectric locally and on a device when
   instrumented; split sources into host, device, and shared sets so nothing forks.
7. **Ship it as a dependency.** Publish a first-class `<module>-testing` artifact that declares an
   `api` dependency on the module under test, so consumers get it (and the contract) by adding one
   line to their test configuration.
8. **Restore global state in `finally`,** and add a test that proves the restore happened. Leaked
   static state is the most common source of cross-test contamination.
9. **Make failures self-explanatory.** Give matchers human-readable descriptions, and mark
   unsupported surfaces with a loud `TODO()` rather than a silent no-op.

## Recommendation: do the same for your platform

The throughline is one decision, made well each time: fake the smallest seam you can, and substitute
it as honestly as possible, usually with a hand-written fake behind a small public surface. What
makes that worth doing is Robolectric. It turns "test against the platform" from an emulator job
measured in seconds into a JVM job measured in milliseconds, and the payoff lands in the consumer's
project as nothing more exotic than a test dependency: they add your `-testing` artifact, write
`@RunWith(AndroidJUnit4::class)` tests against your real API, and run them on every commit.

So, plainly: if you ship a custom AOSP-expanded feature, a customized AOSP platform, or any module
whose APIs developers build on, ship them a `-testing` companion library built on Robolectric. The
cost is modest: a fake or two plus a rule or a harness, and the eleven libraries above are a working
blueprint for every shape it can take. Android's own guidance, read from the consumer's side, is to
"check with the authors … for … fakes … you can reliably depend on." If you own the platform or
module, you are those authors, and Robolectric is how you answer.

## References

- Robolectric: [how it works][architecture], [configuration][configuring], [native
  graphics][graphicsmode], and [getting started][getting-started].
- Android testing guidance: [test fundamentals][fundamentals], [test doubles][test-doubles],
  [Robolectric strategies][strategies].
- Platform test libraries worth modeling yours on: [Car app-testing][car-testing], [Health Connect
  connect-testing][health-connect].
- Every source link in the case studies points at the AndroidX `frameworks/support` tree on Android
  Code Search.

[fundamentals]: https://developer.android.com/training/testing/fundamentals
[test-doubles]: https://developer.android.com/training/testing/fundamentals/test-doubles
[architecture]: https://robolectric.org/architecture/
[configuring]: https://robolectric.org/configuring/
[graphicsmode]: https://robolectric.org/javadoc/4.15/org/robolectric/annotation/GraphicsMode.html
[getting-started]: https://robolectric.org/getting-started/
[strategies]: https://developer.android.com/training/testing/local-tests/robolectric
[car-testing]: https://developer.android.com/training/cars/apps/library/test-library
[health-connect]: https://developer.android.com/health-and-fitness/health-connect/test/unit-tests
[idling]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/ui/ui-test/src/androidMain/kotlin/androidx/compose/ui/test/RobolectricIdlingStrategy.android.kt
[serviceloader]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:xr/runtime/runtime/src/main/kotlin/androidx/xr/runtime/ServiceLoaderExt.kt
[appfunctiontestrule]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:appfunctions/appfunctions-testing/src/main/java/androidx/appfunctions/testing/AppFunctionTestRule.kt
[testnavhost]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-testing/src/androidMain/kotlin/androidx/navigation/testing/TestNavHostController.android.kt
[viewmodelowner]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:lifecycle/lifecycle-viewmodel-testing/src/commonMain/kotlin/androidx/lifecycle/viewmodel/testing/internal/ViewModelScenarioOwner.kt
[windowrule]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:window/window-testing/src/main/java/androidx/window/testing/layout/WindowLayoutInfoPublisherRule.kt
[testscheduler]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:work/work-testing/src/main/java/androidx/work/testing/TestScheduler.kt
[testcarcontext]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:car/app/app-testing/src/main/java/androidx/car/app/testing/TestCarContext.java
[testtileclient]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:wear/tiles/tiles-testing/src/main/java/androidx/wear/tiles/testing/TestTileClient.kt
[glanceunit]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:glance/glance-appwidget-testing/src/main/java/androidx/glance/appwidget/testing/unit/GlanceAppWidgetUnitTest.kt
[protolayout]: https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:wear/protolayout/protolayout-testing/src/main/java/androidx/wear/protolayout/testing/LayoutElementAssertionsProvider.kt
