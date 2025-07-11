<!-- markdownlint-disable-next-line MD033 -->
## Migrating to 3.6<a name="migrating-from-35-to-36"></a>

Previously, Robolectric's [`Display`][display-documentation] and
[`DisplayMetrics`][display-metrics-documentation] dimensions defaulted to 480&times;800px,
regardless of screen size and density. Now they match the requested configuration, which defaults to
320&times;470dp mdpi. Tests which rely on the old dimensions for layouts, pixel math, etc. can be
modified for new dimensions, or by pinning them to the old size:
`@Config(qualifiers = "w480dp-h800dp")`.

| 3.5                                   | 3.6                                                                               |
|---------------------------------------|-----------------------------------------------------------------------------------|
| `Shadow.newInstanceOf(Display.class)` | [`ShadowDisplay.getDefaultDisplay()`][shadow-display-get-default-display-javadoc] |

---

<!-- markdownlint-disable-next-line MD033 -->
## Migrating to 3.4<a name="migrating-from-33-to-34"></a>

### Dependencies

Robolectric tests should now be compiled with Java 8 and Android API 26.

### PackageManager

We've change [`PackageManager`][package-manager-documentation] simulation to follow the standard
Shadow pattern as with other framework classes. You can use
[`ShadowPackageManager`][shadow-package-manager-javadoc] where you previously used
`RobolectricPackageManager`.

| 3.3 (was `@Deprecated`)                                 | 3.4                                                            |
|---------------------------------------------------------|----------------------------------------------------------------|
| `org.robolectric.res.builder.RobolectricPackageManager` | [`ShadowPackageManager`][shadow-package-manager-javadoc]       |
| `RuntimeEnvironment.getRobolectricPackageManager()`     | `shadowOf(RuntimeEnvironment.application.getPackageManager())` |

If you were using `RuntimeEnvironment.setRobolectricPackageManager()` to install a custom
`PackageManager`, you should move your custom behavior to a subclass of
[`ShadowApplicationPackageManager`][shadow-application-package-manager-javadoc] and install it as a
shadow (using [`@Config`][config-javadoc] and possibly
`@Implements(inheritImplementationMethods = true)`) instead.

### `ActivityController.attach()`

The deprecated and redundant `attach()` method has been removed from
[`ActivityController`][activity-controller-javadoc],
[`FragmentController`][fragment-controller-javadoc], and
[`ServiceController`][service-controller-javadoc]. To migrate, remove calls to this method.

### Other Stuff

* Because [`SharedPreferences`][shared-preferences-documentation] now uses real Android framework
  code, [Mockito][mockito] mocks and spies can lead to tests hanging. Rather than spying on
  `SharedPreferences`, directly assert against its state.

---

<!-- markdownlint-disable-next-line MD033 -->
## Migrating to 3.3<a name="migrating-from-32-to-33"></a>

### Moved classes

To simplify classloader logic and cleanup packages, some classes have moved. The old class locations
are `@Deprecated` and will be removed in 3.4.

| 3.2 (now `@Deprecated`)                                    | 3.3                                                                                                   |
|------------------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| `org.robolectric.internal.Shadow`                          | [`org.robolectric.shadow.api.Shadow`][shadow-javadoc]                                                 |
| `org.robolectric.internal.ShadowExtractor.extract()`       | [`org.robolectric.shadow.api.Shadow.extract()`][shadow-extract-javadoc]                               |
| `org.robolectric.util.ActivityController`                  | [`org.robolectric.android.controller.ActivityController`][activity-controller-javadoc]                |
| `org.robolectric.util.ContentProviderController`           | [`org.robolectric.android.controller.ContentProviderController`][content-provider-controller-javadoc] |
| `org.robolectric.util.FragmentController`                  | [`org.robolectric.android.controller.FragmentController`][fragment-controller-javadoc]                |
| `org.robolectric.util.IntentServiceController`             | [`org.robolectric.android.controller.IntentServiceController`][intent-service-controller-javadoc]     |
| `org.robolectric.util.ServiceController`                   | [`org.robolectric.android.controller.ServiceController`][service-controller-javadoc]                  |
| `org.robolectric.util.AccessibilityUtil`                   | `org.robolectric.android.AccessibilityUtil`                                                           |
| `org.robolectric.util.ApplicationTestUtil`                 | `org.robolectric.android.ApplicationTestUtil`                                                         |
| `org.robolectric.res.builder.StubPackageManager`           | `org.robolectric.android.StubPackageManager` ⚠️                                                       |
| `org.robolectric.res.builder.XmlResourceParserImpl`        | [`org.robolectric.android.XmlResourceParserImpl`][xml-resource-parser-impl-javadoc] ⚠️                |
| `org.robolectric.internal.fakes.RoboCharsets`              | `org.robolectric.android.fakes.RoboCharsets` ⚠️                                                       |
| `org.robolectric.internal.fakes.RoboExtendedResponseCache` | [`org.robolectric.android.fakes.RoboExtendedResponseCache`][robo-extended-response-cache-javadoc] ⚠️  |
| `org.robolectric.util.concurrent.RoboExecutorService`      | [`org.robolectric.android.util.concurrent.RoboExecutorService`][robo-executor-service-javadoc] ⚠️     |

⚠️ Only use these classes if you really need too

### `PackageManager`

We have begun the process of switching from using a subclass of
[`PackageManager`][package-manager-documentation] towards `PackageManager` being implemented by a
standard shadow, as we do for the rest of the framework. This is for a number of reasons:

* It is more consistent with the way other framework code is handled.
* A shadow will allow users' tests to build against any version of Android.
* Switching to a shadow will allow us to defer parsing the manifest until the test or code under
  test makes calls to the `PackageManager`.

This should all be backwards compatible for the 3.3 release, but now you can start migrating your
code.

Before, the Robolectric class `DefaultPackageManager` implemented all `PackageManager`
functionality. If you wanted to change any of its behavior, you'd extend `DefaultPackageManager` (or
`StubPackageManager`) and override whichever methods you liked. Test-related setup was achieved by
calling `RuntimeEnvironment.getRobolectricPackageManager()`, which had extra methods for modifying
its behavior.

As of 3.3, Robolectric uses the normal Android `ApplicationPackageManager`, and shadows all of its
methods, causing it to delegate to an instance of `DefaultPackageManager`, which works as before.
You can still replace it with your own subclass of `PackageManager` if you like, but that's
deprecated. Instead of doing that, put your custom behavior in a subclass of
[`ShadowApplicationPackageManager`][shadow-application-package-manager-javadoc]. For test-related
setup, you can still access it through `RuntimeEnvironment.getRobolectricPackageManager()`, but you
should start using [`shadowOf(PackageManager)`][shadow-of-package-manager-javadoc] instead. Note
that we've implemented quite a bit more of `PackageManager`, so you might not need any custom code
any longer.

Starting with 3.4, `DefaultPackageManager` will be removed and its functionality will be moved into
`ShadowApplicationPackageManager`. In general, we recommand using Android Framework APIs where
possible.

| 3.2 (now `@Deprecated`)                                                              | 3.3                                                                                  |
|--------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| `RobolectricPackageManager rpm = RuntimeEnvironment.getRobolectricPackageManager();` | `ShadowPackageManager shadowPackageManager = shadowOf(context.getPackageManager());` |
| `PackageManager packageManager = RuntimeEnvironment.getPackageManager();`            | `PackageManager packageManager = context.getPackageManager();`                       |
| `RuntimeEnvironment.setRobolectricPackageManager(customPackageManager);`             | Use a custom shadow instead! See below.                                              |

Replace subclasses of `DefaultPackageManager` by a custom shadow (and be a good citizen by
contributing your enhancements upstream 🙂):

/// tab | Java

```java
@Implements(value = ApplicationPackageManager.class, inheritImplementationMethods = true)
public class MyCustomPackageManager extends ShadowApplicationPackageManager {
}
```

///

/// tab | Kotlin

```kotlin
@Implements(value = ApplicationPackageManager::class, inheritImplementationMethods = true)
class MyCustomPackageManager : ShadowApplicationPackageManager
```

///

If you are using a custom subclass of `DefaultPackageManager` to implement functionality missing in
Robolectric, check again as part of this work we've added support for a bunch more widely-used
`PackageManager` features, and it might be now possible to completely remove your custom subclass.

### Deprecated Classes & Methods

| 3.2 (now `@Deprecated`)                                     | 3.3                                                                                                                                        |
|-------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| `org.robolectric.res.builder.RobolectricPackageManager`     | [`org.robolectric.shadows.ShadowPackageManager`][shadow-package-manager-javadoc]                                                           |
| `RuntimeEnvironment.getRobolectricPackageManager()`         | [`shadowOf(RuntimeEnvironment.application.getPackageManager())`][shadow-of-package-manager-javadoc]                                        |
| `shadowOf(imageView).getImageResourceId()`                  | [`shadowOf(imageView.getDrawable)`][shadow-of-drawable-javadoc].[`getCreatedFromResId()`][shadow-drawable-get-created-from-res-id-javadoc] |
| `shadowOf(notification).getProgressBar().isIndeterminate()` | [`shadowOf(notification)`][shadow-of-notification-javadoc].[`isIndeterminate()`][shadow-notification-is-indeterminate-javadoc]             |

The following methods and classes are deprecated will be removed in 3.4:

* `RuntimeEnvironment.getPackageManager()`
* `RuntimeEnvironment.getRobolectricPackageManager()`
* `RuntimeEnvironment.setRobolectricPackageManager()`
* `DefaultPackageManager`
* `StubPackageManager`
* `RobolectricPackageManager`

### New Gradle integration

Starting with Robolectric 3.3 and Android Studio 3.0 alpha 5, we've included better integration with
the tool chain. Resources, assets and `AndroidManifest.xml` are treated as first-class citizens and
processed by the build system for correctness, performance and stability between releases. Read
more [here](getting-started.md).

---

<!-- markdownlint-disable-next-line MD033 -->
## Migrating to 3.2<a name="migrating-from-31-to-32"></a>

### Programmatic Configuration

If you were providing custom configuration by subclassing and overriding methods on
[`RobolectricTestRunner`][robolectric-test-runner-javadoc], you'll need to make some changes.

`RobolectricTestRunner.getConfigProperties()` should be replaced by overriding
[`RobolectricTestRunner.buildGlobalConfig()`][robolectric-test-runner-build-global-config-javadoc].

#### Old code

/// tab | Java

```java
public class MyTestRunner extends RobolectricTestRunner {
  @Override protected Properties getConfigProperties() {
    Properties props = new Properties();
    props.setProperty("sdk", "23");
    return props;
  }
}
```

///

/// tab | Kotlin

```kotlin
class MyTestRunner : RobolectricTestRunner {
  override protected fun getConfigProperties(): Properties {
    val props = Properties()
    props.setProperty("sdk", "23")
    return props
  }
}
```

///

#### New code

/// tab | Java

```java
public class MyTestRunner extends RobolectricTestRunner {
  @Override protected Config buildGlobalConfig() {
    return new Config.Builder().setSdk(23).build();
  }
}
```

///

/// tab | Kotlin

```kotlin
class MyTestRunner : RobolectricTestRunner {
  override protected fun buildGlobalConfig(): Config {
    return Config.Builder().setSdk(23).build()
  }
}
```

///

### Package-Level Configuration

If you are using a [`robolectric.properties`](configuring.md#robolectricproperties-file) file to
configure all tests, the expected location of the file has been changed.

| 3.1                                         | 3.2                                                      |
|---------------------------------------------|----------------------------------------------------------|
| `src/test/resources/robolectric.properties` | `src/test/resources/your/package/robolectric.properties` |

---

<!-- markdownlint-disable-next-line MD033 -->
## Migrating to 3.1<a name="migrating-from-30-to-31"></a>

### Changes

* To construct an Android components such as an [`Activity`][activity-documentation], a
  [`Service`][service-documentation] or a [`ContentProvider`][content-provider-documentation], you
  must now use the Robolectric APIs such as
  [`Robolectric.buildActivity()`][robolectric-build-activity-javadoc],
  [`Robolectric.buildService()`][robolectric-build-service-javadoc], or
  [`Robolectric.buildContentProvider()`][robolectric-build-content-provider-javadoc], or the
  corresponding `setup*()` methods; you should not create new instances of these classes yourself.
  Calling these methods will create an instance of the component and attach its base
  [`Context`][context-documentation]. This is now necessary as we've removed code shadowing
  `Context` and [`ContextWrapper`][context-wrapper-documentation] in favor of using real framework
  code to improve fidelity.

/// tab | Java

```java
Robolectric.buildService(MyService.class).create().get();
Robolectric.setupContentProvider(MyContentProvider.class);
```

///

/// tab | Kotlin

```kotlin
Robolectric.buildService(MyService::class).create().get()
Robolectric.setupContentProvider(MyContentProvider::class)
```

///

* We've removed shadow methods where they duplicate the functionality of the Android APIs. In
  general, prefer calling Android framework APIs over Robolectric shadows where possible.

| 3.0                                                            | 3.1                                                                                                                                              |
|----------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| `ShadowApplication.getInstance().getContentResolver()`         | [`RuntimeEnvironment.application`][runtime-environment-application-javadoc].[`getContentResolver()`][context-get-content-resolver-documentation] |
| `ShadowApplication.getInstance().getPackageManager()`          | [`RuntimeEnvironment.application`][runtime-environment-application-javadoc].[`getPackageManager()`][context-get-package-manager-documentation]   |
| `ShadowApplication.getInstance().getResources()`               | [`RuntimeEnvironment.application`][runtime-environment-application-javadoc].[`getResources()`][context-get-resources-documentation]              |
| `ShadowApplication.getInstance().getString()`                  | [`RuntimeEnvironment.application`][runtime-environment-application-javadoc].[`getString()`][context-get-string-documentation]                    |
| `ShadowApplication.getInstance().resetResources()`             | [`RuntimeEnvironment.application`][runtime-environment-application-javadoc].`resetResources()`                                                   |
| `ShadowApplication.getInstance().getAssets()`                  | [`RuntimeEnvironment.application`][runtime-environment-application-javadoc].[`getAssets()`][context-get-assets-documentation]                    |
| `ShadowPreferenceManager.getDefaultSharedPreferences()`        | [`PreferenceManager.getDefaultSharedPreferences()`][preference-manager-get-default-shared-preferences-documentation]                             |
| `shadowOf(shadowOf(notification).getStyle()).getSummaryText()` | [`shadowOf(notification)`][shadow-of-notification-javadoc].[`getContentText()`][shadow-notification-get-content-text-javadoc]                    |

* [`RoboMenu`][robo-menu-javadoc] now requires a `Context` passed into its constructor. Previously
  it was internally using
  [`RuntimeEnvironment.application`][runtime-environment-application-javadoc] which you can use as a
  sensible default.
* `.equals()` and `.hashCode()` methods have been removed from shadows to stop them incorrectly
  providing an alternative equality of the shadowed object. For example, `Intent.equals()` now
  behaves as the framework method. Instead of relying on a generically shadowed `equals()` method
  with a vague equality rule, prefer to make assertions on specific fields of interest to the test.
  We recommend using [`square/assertj-android`][square-assertj-android] to make assertions clear.
* Custom shadows now require the `public` access modifier on methods in the shadow class.

### 3.1.1 Changes

* `RoboAttributeSet` is deprecated and uses should be replaced with
  [`Robolectric.buildAttributeSet()`][robolectric-build-attribute-set-javadoc].
* `RobolectricGradleTestRunner` is deprecated and uses should be replaced with
  [`RobolectricTestRunner`][robolectric-test-runner-javadoc].

---

<!-- markdownlint-disable-next-line MD033 -->
## Migrating to 3.0<a name="migrating-from-24-to-30"></a>

### New Features

* Support for API 19 (KitKat)
* Support for API 21 (Lollipop)
* Custom test runner for Gradle / Android Studio:

/// tab | Java

```java
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
```

///

/// tab | Kotlin

```kotlin
@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class)
```

///

### Major Changes

| 2.4                                                                                         | 3.0                                                                                                                                                                                 |
|---------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `Robolectric.application`                                                                   | `RuntimeEnvironment.application`                                                                                                                                                    |
| `Robolectric.shadowOf`                                                                      | `Shadows.shadowOf`                                                                                                                                                                  |
| `Robolectric.Reflection.setFinalStaticField`                                                | `ReflectionHelpers.setStaticField`                                                                                                                                                  |
| `org.robolectric.Robolectric.<xxx>Looper`                                                   | `org.robolectric.shadows.ShadowLooper.<xxx>Looper`                                                                                                                                  |
| `org.robolectric.Robolectric.getBackgroundScheduler`                                        | `org.robolectric.Robolectric.getBackgroundThreadScheduler`                                                                                                                          |
| `org.robolectric.Robolectric.runBackgroundTasks`                                            | `org.robolectric.Robolectric.getBackgroundThreadScheduler().advanceBy(0)` (also `org.robolectric.Robolectric.flushBackgroundThreadScheduler` will run delayed background tasks too) |
| `org.robolectric.Robolectric.getUiThreadScheduler`                                          | `org.robolectric.Robolectric.getForegroundThreadScheduler`                                                                                                                          |
| `org.robolectric.Robolectric.runUiThreadTasks`                                              | `org.robolectric.shadows.ShadowLooper.runUiThreadTasks`                                                                                                                             |
| `org.robolectric.Robolectric.runUiThreadTasksIncludingDelayedTasks`                         | `org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks` or `org.robolectric.Robolectric.flushForegroundThreadScheduler`                                        |
| `org.robolectric.Robolectric.getShadowApplication`                                          | `org.robolectric.shadows.ShadowApplication.getInstance`                                                                                                                             |
| `FragmentTestUtil.startFragment`(v4/v11)                                                    | `SupportFragmentTestUtil.startFragment` (v4) / `FragmentTestUtil.startFragment` (v11)                                                                                               |
| `org.robolectric.tester.android.view.TestMenuItem`                                          | `org.robolectric.fakes.RoboMenuItem`                                                                                                                                                |
| `ActivityController.of`                                                                     | `Robolectric.buildActivity`                                                                                                                                                         |
| `org.robolectric.Config.properties`                                                         | `robolectric.properties`                                                                                                                                                            |
| `@Config(emulateSdk=...)` / `@Config(reportSdk=...)`                                        | `@Config(sdk=...)`                                                                                                                                                                  |
| `org.robolectric.shadows.ShadowHandler`                                                     | `org.robolectric.shadows.ShadowLooper`                                                                                                                                              |
| `org.robolectric.shadows.ShadowSettings.SettingsImpl`                                       | `org.robolectric.shadows.ShadowSettings.ShadowSystem`                                                                                                                               |
| `Robolectric.packageManager = instance`                                                     | `RuntimeEnvironment.setRobolectricPackageManager(instance)`                                                                                                                         |
| `org.robolectric.res.builder.RobolectricPackageManager` changed from `class` to `interface` | `org.robolectric.res.builder.DefaultPackageManager`                                                                                                                                 |
| `org.robolectric.shadows.ShadowMenuInflater`                                                | ?                                                                                                                                                                                   |
| `org.robolectric.Robolectric.clickOn`                                                       | `org.robolectric.shadows.ShadowView.clickOn`                                                                                                                                        |

* `Robolectric.shadowOf_` has been removed. Similar functionality exists in
  `ShadowExtractor.extract`.

### Modules

> [!NOTE]
> Shadows for non-core Android classes have moved out of the main Robolectric module. If you want to
> use those shadows, you'll need to include the requisite module.

#### `org.robolectric:robolectric`

Main "core" module for Robolectric 3.0.

/// tab | Groovy

```groovy
testCompile 'org.robolectric:robolectric:3.0'
```

///

/// tab | Kotlin

```kotlin
testCompile("org.robolectric:robolectric:3.0")
```

///

Some of the shadows in Robolectric have been split out into separate modules to reduce the number of
transitive dependencies imposed on projects using Robolectric. If you want to use any of these
shadows, add the needed artifacts below to your build.

#### `org.robolectric:shadows-support-v4`

Shadows for classes in the Android `support-v4` library.

/// tab | Groovy

```groovy
testCompile 'org.robolectric:shadows-support-v4:3.0'
```

///

/// tab | Kotlin

```kotlin
testCompile("org.robolectric:shadows-support-v4:3.0")
```

///

#### `org.robolectric:shadows-httpclient`
  
Shadows for classes in Apache HTTP client. This includes methods like
`Robolectric.getLatestSentHttpRequest`. These methods have moved to
[`FakeHttp.getLatestSentHttpRequest`][fake-http-get-latest-sent-http-request-javadoc].

/// tab | Groovy

```groovy
testCompile 'org.robolectric:shadows-httpclient:3.0'
```

///

/// tab | Kotlin

```kotlin
testCompile("org.robolectric:shadows-httpclient:3.0")
```

///

#### `org.robolectric:shadows-maps`

Shadows for classes in Google Maps.

/// tab | Groovy

```groovy
testCompile 'org.robolectric:shadows-maps:3.0'
```

///

/// tab | Kotlin

```kotlin
testCompile("org.robolectric:shadows-maps:3.0")
```

///

[activity-controller-javadoc]: javadoc/latest/org/robolectric/android/controller/ActivityController.html
[activity-documentation]: https://developer.android.com/reference/android/app/Activity
[config-javadoc]: javadoc/latest/org/robolectric/annotation/Config.html
[content-provider-controller-javadoc]: javadoc/latest/org/robolectric/android/controller/ContentProviderController.html
[content-provider-documentation]: https://developer.android.com/reference/android/content/ContentProvider
[context-documentation]: https://developer.android.com/reference/android/content/Context
[context-get-assets-documentation]: https://developer.android.com/reference/android/content/Context#getAssets()
[context-get-content-resolver-documentation]: https://developer.android.com/reference/android/content/Context#getContentResolver()
[context-get-package-manager-documentation]: https://developer.android.com/reference/android/content/Context#getPackageManager()
[context-get-resources-documentation]: https://developer.android.com/reference/android/content/Context#getResources()
[context-get-string-documentation]: https://developer.android.com/reference/android/content/Context#getString(int)
[context-wrapper-documentation]: https://developer.android.com/reference/android/content/ContextWrapper
[display-documentation]: https://developer.android.com/reference/android/view/Display
[display-metrics-documentation]: https://developer.android.com/reference/android/util/DisplayMetrics
[fake-http-get-latest-sent-http-request-javadoc]: javadoc/latest/org/robolectric/shadows/httpclient/FakeHttp.html#getLatestSentHttpRequest()
[fragment-controller-javadoc]: javadoc/latest/org/robolectric/android/controller/FragmentController.html
[intent-service-controller-javadoc]: javadoc/latest/org/robolectric/android/controller/IntentServiceController.html
[mockito]: https://site.mockito.org/
[package-manager-documentation]: https://developer.android.com/reference/android/content/pm/PackageManager
[preference-manager-get-default-shared-preferences-documentation]: https://developer.android.com/reference/android/preference/PreferenceManager#getDefaultSharedPreferences(android.content.Context)
[robo-executor-service-javadoc]: javadoc/latest/org/robolectric/android/util/concurrent/RoboExecutorService.html
[robo-extended-response-cache-javadoc]: javadoc/latest/org/robolectric/fakes/RoboExtendedResponseCache.html
[robo-menu-javadoc]: javadoc/latest/org/robolectric/fakes/RoboMenu.html
[robolectric-build-activity-javadoc]: javadoc/latest/org/robolectric/Robolectric.html#buildActivity(java.lang.Class)
[robolectric-build-attribute-set-javadoc]: javadoc/latest/org/robolectric/Robolectric.html#buildAttributeSet()
[robolectric-build-content-provider-javadoc]: javadoc/latest/org/robolectric/Robolectric.html#buildContentProvider(java.lang.Class)
[robolectric-build-service-javadoc]: javadoc/latest/org/robolectric/Robolectric.html#buildService(java.lang.Class)
[robolectric-test-runner-build-global-config-javadoc]: javadoc/latest/org/robolectric/RobolectricTestRunner.html#buildGlobalConfig()
[robolectric-test-runner-javadoc]: javadoc/latest/org/robolectric/RobolectricTestRunner.html
[runtime-environment-application-javadoc]: javadoc/latest/org/robolectric/RuntimeEnvironment.html#application
[service-controller-javadoc]: javadoc/latest/org/robolectric/android/controller/ServiceController.html
[service-documentation]: https://developer.android.com/reference/android/app/Service
[shadow-application-package-manager-javadoc]: javadoc/latest/org/robolectric/shadows/ShadowApplicationPackageManager.html
[shadow-display-get-default-display-javadoc]: javadoc/latest/org/robolectric/shadows/ShadowDisplay.html#getDefaultDisplay()
[shadow-drawable-get-created-from-res-id-javadoc]: javadoc/latest/org/robolectric/shadows/ShadowDrawable.html#getCreatedFromResId()
[shadow-extract-javadoc]: javadoc/latest/org/robolectric/shadow/api/Shadow.html#extract(java.lang.Object)
[shadow-javadoc]: javadoc/latest/org/robolectric/shadow/api/Shadow.html
[shadow-notification-get-content-text-javadoc]: javadoc/latest/org/robolectric/shadows/ShadowNotification.html#getContentText()
[shadow-notification-is-indeterminate-javadoc]: javadoc/latest/org/robolectric/shadows/ShadowNotification.html#isIndeterminate()
[shadow-of-drawable-javadoc]: javadoc/latest/org/robolectric/Shadows.html#shadowOf(android.graphics.drawable.Drawable)
[shadow-of-notification-javadoc]: javadoc/latest/org/robolectric/Shadows.html#shadowOf(android.app.Notification)
[shadow-of-package-manager-javadoc]: javadoc/latest/org/robolectric/Shadows.html#shadowOf(android.content.pm.PackageManager)
[shadow-package-manager-javadoc]: javadoc/latest/org/robolectric/shadows/ShadowPackageManager.html
[shared-preferences-documentation]: https://developer.android.com/reference/android/content/SharedPreferences
[square-assertj-android]: https://github.com/square/assertj-android
[xml-resource-parser-impl-javadoc]: javadoc/latest/org/robolectric/android/XmlResourceParserImpl.html
