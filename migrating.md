---
title: Migration Guide
page_title: Robolectric Migration Guide
group: User Guide
order: 7
toc: true
---

## Migrating from 3.2 to 3.3

---

| 3.2 | 3.3 |
| ------------- | ------------- |
| `org.robolectric.internal.Shadow` | `org.robolectric.shadow.api.Shadow` |
| `org.robolectric.util.ActivityController` | `org.robolectric.android.controller.ActivityController` |
| `org.robolectric.util.ContentProviderController` | `org.robolectric.android.controller.ContentProviderController` |
| `org.robolectric.util.FragmentController` | `org.robolectric.android.controller.FragmentController` |
| `org.robolectric.util.IntentServiceController` | `org.robolectric.android.controller.IntentServiceController` |
| `org.robolectric.util.ServiceController` | `org.robolectric.android.controller.ServiceController` |
| `` | `org.robolectric.android.AccessibilityUtil` |
| `` | `org.robolectric.android.ApplicationTestUtil` |
| `` | `org.robolectric.android.StubPackageManager` |
| `` | `org.robolectric.android.XmlResourceParserImpl:` |
| `org.robolectric.internal.fakes.RoboCharsets` | `org.robolectric.android.fakes.RoboCharsets` |
| `org.robolectric.internal.fakes.RoboExtendedResponseCache` | `org.robolectric.android.fakes.RoboExtendedResponseCache` |
| `` | `org.robolectric.android.util.concurrent.RoboExecutorService` |
| `` | `` |
| `` | `` |

---

tbd:

`RobolectricTestRunner.*`

`AccountManager.removeAccount()` -> `AccountManager.removeAccount().getResult()`

---

## Migrating from 3.1 to 3.2

### Programmatic Configuration
If you were providing custom configuration by subclassing and overriding methods on `RobolectricTestRunner`, you'll need to make some changes.

`RobolectricTestRunner.getConfigProperties()` has moved to `ConfigMerger.getConfigProperties(String)`. But what you probably actually want to do is override `RobolectricTestRunner.buildGlobalConfig()`.

Old code:
```java
class MyTestRunner extends RobolectricTestRunner {
  @Override protected Properties getConfigProperties() {
    Properties props = new Properties();
    props.setProperty("sdk", "23");
    return props;
  }
}
```

New code:

```java
class MyTestRunner extends RobolectricTestRunner {
  @Override protected Config buildGlobalConfig() {
    return new Config.Builder().setSdk(23).build();
  }
}
```

### Package-level Configuration
If you are using `robolectric.properties` file to configure all tests, the expected location of the file has been changed.

- 3.1: `src/test/resources/robolectric.properties`
- 3.2: `src/test/resources/your/package/path/robolectric.properties`

---

## Migrating from 3.0 to 3.1

### Changes
* To construct Android components such as `Activity`, `Service` and `ContentProvider` classes you must now use the Robolectric APIs such as `Robolectric.buildService()`, `Robolectric.buildActivity()` or `Robolectric.buildContentProvider()` or the `setup*()` variants; you should not create new instances of these classes yourself. Calling these methods will create an instance of the component and attach its base context. This is now necessary as we've removed code shadowing `Context` and `ContextWrapper` in favor of using real framework code to improve fidelity.

```java
Robolectric.buildService(MyService.class).create().get();
Robolectric.setupContentProvider(MyContentProvider.class);
```

* We've removed shadow methods where they duplicate the functionality of the Android APIs. In general, prefer calling Android framework APIs over Robolectric shadows where possible.

| 3.0 | 3.1 |
| ------------- | ------------- |
| `ShadowApplication.getInstance().getContentResolver()` | `RuntimeEnvironment.application.getContentResolver()` |
| `ShadowApplication.getInstance().getPackageManager()` | `RuntimeEnvironment.application.getPackageManager()` |
| `ShadowApplication.getInstance().getResources()` | `RuntimeEnvironment.application.getResources()` |
| `ShadowApplication.getInstance().getString()`  | `RuntimeEnvironment.application.getString()` |
| `ShadowApplication.getInstance().resetResources()`  | `RuntimeEnvironment.application.resetResources()` |
| `ShadowApplication.getInstance().getAssets()`  | `RuntimeEnvironment.application.getAssets()` |
| `ShadowPreferenceManager.getDefaultSharedPreferences()`  | `PreferenceManager.getDefaultSharedPreferences()` |
| `shadowOf(shadowOf(notification).getStyle()).getSummaryText()`  | `shadowOf(notification).getContentText()` |

* RoboMenu now requires a context passed into its constructor. Previously it was internally using RuntimeEnvironment.application which you can use as a sensible default.

* `.equals()` and `.hashCode()` methods have been removed from shadows to stop them incorrectly providing an alternative equality of the shadowed object. For example, `Intent.equals()` now behaves as the framework method. Instead of relying on an generically shadowed `equals()` method with a vague equality rule, prefer to make assertions on specific fields of interest to the test. We recommend using https://github.com/square/assertj-android to make assertions clear.

* Custom shadows now require the `public` access modifier on methods in the shadow class.

### 3.1.1 Changes

* RoboAttributeSet is deprecated and uses should be replaced with Robolectric.buildAttributeSet().
* RobolectricGradleTestRunner is deprecated and uses should be replaced with RobolectricTestRunner.


---

## Migrating from 2.4 to 3.0

### New Features

* Support for API 19 (KitKat)

* Support for API 21 (Lollipop)

* Custom test runner for Gradle / Android Studio:
 
```
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
```



### Major Changes

| 2.4 | 3.0 |
| ------------- | ------------- |
| `Robolectric.application` | `RuntimeEnvironment.application` |
| `Robolectric.shadowOf` | `Shadows.shadowOf` |
| `Robolectric.Reflection.setFinalStaticField` | `ReflectionHelpers.setStaticField` |
| `org.robolectric.Robolectric.<xxx>Looper`  | `org.robolectric.shadows.ShadowLooper.<xxx>Looper` |
| `org.robolectric.Robolectric.getBackgroundScheduler`  | `org.robolectric.Robolectric.getBackgroundThreadScheduler` |
| `org.robolectric.Robolectric.runBackgroundTasks` | `org.robolectric.Robolectric.getBackgroundThreadScheduler().advanceBy(0)` (also `org.robolectric.Robolectric.flushBackgroundThreadScheduler` will run delayed background tasks too) |
| `org.robolectric.Robolectric.getUiThreadScheduler` | `org.robolectric.Robolectric.getForegroundThreadScheduler` |
| `org.robolectric.Robolectric.runUiThreadTasks` | `org.robolectric.shadows.ShadowLooper.runUiThreadTasks` |
| `org.robolectric.Robolectric.runUiThreadTasksIncludingDelayedTasks` | `org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks`<br>or<br>`org.robolectric.Robolectric.flushForegroundThreadScheduler` |
| `org.robolectric.Robolectric.getShadowApplication` | `org.robolectric.shadows.ShadowApplication.getInstance` |
| `FragmentTestUtil.startFragment`(v4/v11) | `SupportFragmentTestUtil.startFragment` (v4)<br>`FragmentTestUtil.startFragment` (v11) |
| `org.robolectric.tester.android.view.TestMenuItem` | `org.robolectric.fakes.RoboMenuItem` |
| `ActivityController.of` | `Robolectric.buildActivity` |
| `org.robolectric.Config.properties` | `robolectric.properties` |
| `@Config(emulateSdk=...)` / `@Config(reportSdk=...)` | `@Config(sdk=...)` |
| `org.robolectric.shadows.ShadowHandler` | `org.robolectric.shadows.ShadowLooper` |
| `org.robolectric.shadows.ShadowSettings.SettingsImpl` | `org.robolectric.shadows.ShadowSettings.ShadowSystem` |
| `Robolectric.packageManager = instance` | `RuntimeEnvironment.setRobolectricPackageManager(instance)` |
| `org.robolectric.res.builder.RobolectricPackageManager`<br>changed from `class` to `interface` | `org.robolectric.res.builder.DefaultPackageManager` |
| `org.robolectric.shadows.ShadowMenuInflater` | ? |
| `org.robolectric.Robolectric.clickOn` | `org.robolectric.shadows.ShadowView.clickOn` |

* `Robolectric.shadowOf_` has been removed. Similar functionality exists in `ShadowExtractor.extract`.

### Modules

Note: Shadows for non-core Android classes has moved out of the main Robolectric module. If you want to use those shadows, you'll need to include the requisite module.

**`org.robolectric:robolectric:3.0`**

Main "core" module for Robolectric 3.0.

    testCompile 'org.robolectric:robolectric:3.0'

Some of the shadows in Robolectric have been split out into separate modules to reduce the number of transitive dependencies imposed on projects using Robolectric. If you want to use any of these shadows,
simply add the the artifact below to your build.
 
**`org.robolectric:shadows-support-v4:3.0`**

Shadows for classes in the android support-v4 library.

    testCompile 'org.robolectric:shadows-support-v4:3.0'

**`org.robolectric:shadows-httpclient:3.0`**
  
Shadows for classes in Apache HTTP client. This includes methods like `Robolectric.getLatestSentHttpRequest`. These methods have moved to `FakeHttp.getLatestSentHttpRequest`.

    testCompile 'org.robolectric:shadows-httpclient:3.0'

**`org.robolectric:shadows-maps:3.0`**

Shadows for classes in Google maps.

    testCompile 'org.robolectric:shadows-maps:3.0'
