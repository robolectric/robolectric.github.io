## Migrating to 4.15

### Deprecations

| Deprecated symbol                                                            | Replacement                                                                                                                       |
|------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| `org.robolectric:shadows-playservices`                                       | Use APIs provided by Google Player Services libraries                                                                             |
| `ActivityController#configurationChange(Configuration, DisplayMetrics, int)` | `ActivityController#configurationChange(Configuration, DisplayMetrics)`                                                           |
| `AttributeSetBuilder`                                                        | `Xml#asAttributeSet()`                                                                                                            |
| `Config.Builder#setAssetDir()`                                               | See [build system integration][build-system-integration]                                                                          |
| `Config.Builder#setLibraries()`                                              | See [build system integration][build-system-integration]                                                                          |
| `Config.Builder#setPackageName()`                                            | See [build system integration][build-system-integration]                                                                          |
| `Config.Builder#setResourceDir()`                                            | See [build system integration][build-system-integration]                                                                          |
| `Robolectric#buildAttributeSet()`                                            | `Robolectric#getAttributeSetFromXml()`                                                                                            |
| `Robolectric#flushBackgroundThreadScheduler()`                               | `ShadowLooper#runToEndOfTasks()`                                                                                                  |
| `Robolectric#flushForegroundThreadScheduler()`                               | `ShadowLooper#runToEndOfTasks()`                                                                                                  |
| `Robolectric#getBackgroundThreadScheduler()`                                 | This is only needed in the `LEGACY` looper mode. We strongly encourage you to use the `PAUSED` looper mode (which is the default) |
| `Robolectric#getForegroundThreadScheduler()`                                 | This is only needed in the `LEGACY` looper mode. We strongly encourage you to use the `PAUSED` looper mode (which is the default) |
| `RuntimeEnvironment#getMasterScheduler()`                                    | This is only needed in the `LEGACY` looper mode. We strongly encourage you to use the `PAUSED` looper mode (which is the default) |
| `RuntimeEnvironment#setMasterScheduler()`                                    | This is only needed in the `LEGACY` looper mode. We strongly encourage you to use the `PAUSED` looper mode (which is the default) |
| `ShadowApplication#runBackgroundTasks()`                                     | This is only needed in the `LEGACY` looper mode. We strongly encourage you to use the `PAUSED` looper mode (which is the default) |
| `ShadowAssetInputStream#isNinePatch()`                                       | Only use this in `LEGACY` graphics mode                                                                                           |
| `ShadowRoleManager#addHeldRole()`                                            | `ShadowRoleManager#addRoleHolder()`                                                                                               |
| `ShadowRoleManager#addAvailableRole()`                                       | `ShadowRoleManager#addRoleHolder()`                                                                                               |
| `ShadowRoleManager#removeAvailableRole()`                                    | `ShadowRoleManager#removeRoleHolder()`                                                                                            |
| `ShadowRoleManager#removeHeldRole()`                                         | `ShadowRoleManager#removeRoleHolder()`                                                                                            |

### Removals

| Removed symbol                                     | Replacement                                                    |
|----------------------------------------------------|----------------------------------------------------------------|
| `org.robolectric:shadows-multidex`                 | No longer needed with min SDK being 21+                        |
| `AndroidManifest#supportsBinaryResourcesMode()`    | N/A (it always returned `true`)                                |
| `AndroidManifest#supportsLegacyResourcesMode()`    | N/A (it always returned `false`)                               |
| `ConfigMerger`                                     | `Provider<Config>`                                             |
| `DependencyResolver#getLocalArtifactUrls()`        | N/A (overriding `getLocalArtifactUrl()` is enough)             |
| `Fs#fileFromPath(path)`                            | `Fs.fromUrl(path)`                                             |
| `Fs#newFile(file)`                                 | `file.toPath()`                                                |
| `FsFile`                                           | `Path`                                                         |
| `FsFile#getPath()`                                 | `Fs.externalize(path)`                                         |
| `FsFile#join(name)`                                | `path.resolve(name)`                                           |
| `InvokeDynamicClassInstrumentor`                   | `ClassInstrumentor`                                            |
| `IShadow#directlyOn(T, Class<T>)`                  | Use a combination of `Reflector` and `Direct`                  |
| `MavenManifestFactory`                             | See [build system integration][build-system-integration]       |
| `PackageItemData#getClassName()`                   | `PackageItemData#getName()`                                    |
| `ProxyMaker`                                       | Use a combination of `Reflector` and `Direct`                  |
| `Qualifiers#addScreenWidth()`                      | `Configuration#screenWidthDp`                                  |
| `Qualifiers#addSmallestScreenWidth()`              | `Configuration#smallestScreenWidthDp`                          |
| `Qualifiers#getOrientation()`                      | `Configuration#orientation`                                    |
| `Qualifiers#getPlatformVersion()`                  | `Build.VERSION#SDK_INT`                                        |
| `Qualifiers#getScreenWidth()`                      | `Configuration#screenWidthDp`                                  |
| `Qualifiers#getSmallestScreenWidth()`              | `Configuration#smallestScreenWidthDp`                          |
| `RoboSettings`                                     | Read/write the `robolectric.scheduling.global` system property |
| `Scheduler#idleConstantly()`                       | `Scheduler#setIdleState()`                                     |
| `SdkPicker`                                        | `DefaultSdkPicker`                                             |
| `Shadow#directlyOn(T, Class<T>)`                   | Use a combination of `Reflector` and `Direct`                  |
| `ShadowApplication#addWakeLock()`                  | Use the `PowerManager` APIs                                    |
| `ShadowApplication#clearWakeLocks()`               | `ShadowPowerManager#clearWakeLocks()`                          |
| `ShadowApplication#getAppWidgetManager()`          | `Context#getSystemService(Context.APPWIDGET_SERVICE)`          |
| `ShadowApplication#getBluetoothAdapter()`          | `BluetoothAdapter#getDefaultAdapter()`                         |
| `ShadowApplication#getForegroundThreadScheduler()` | Use the `PAUSED` looper mode (which is the default)            |
| `ShadowApplication#getInstance()`                  | `ApplicationProvider#getApplicationContext()`                  |
| `ShadowApplication#getLatestWakeLock()`            | `ShadowPowerManager#getLatestWakeLock()`                       |
| `ShadowApplication#getSingleton()`                 | N/A                                                            |
| `ShadowAssetInputStream#getDelegate()`             | N/A                                                            |
| `ShadowImpl#directlyOn(T, Class<T>)`               | Use a combination of `Reflector` and `Direct`                  |
| `ShadowMap#convertToShadowName()`                  | N/A                                                            |

### Other Changes

- `TestLifecycle` no longer has a generic type.
- Renamed `ActivityData#isClearTaskOnLaungh()` into `ActivityData#isClearTaskOnLaunch()`.
- Renamed `ResourceIds#makeIdentifer()` into `ResourceIds#makeIdentifier()`.

## Migrating to 4.14

### Deprecations

| Deprecated symbol                  | Replacement                                              |
|------------------------------------|----------------------------------------------------------|
| `org.robolectric:shadows-multidex` | No longer needed with min SDK being 21+                  |
| `Implements#looseSignatures()`     | Use either `@ClassName` or `@Implementation(methodName)` |
| `Supercedes`                       | `Supersedes`                                             |

### Removals

| Removed symbol     | Replacement                                                                   |
|--------------------|-------------------------------------------------------------------------------|
| `FragmentTestUtil` | See [Test your fragments](https://developer.android.com/guide/fragments/test) |

## Migrating to 4.13

### Breaking Changes

- This release bumps the min SDK version from 19 to 21, following the move made by the AndroidX
  ecosystem.

### Deprecations

| Deprecated symbol | Replacement                                       |
|-------------------|---------------------------------------------------|
| `Scheduler`       | Use `LooperMode.PAUSED` and `ShadowLooper`'s APIs |

### Removals

| Removed symbol                            | Replacement                      |
|-------------------------------------------|----------------------------------|
| `Qualifiers#addPlatformVersion()`         | N/A                              |
| `RuntimeEnvironment#useLegacyResources()` | N/A (it always returned `false`) |

## Migrating to 4.12

### Deprecations

| Deprecated symbol                      | Replacement             |
|----------------------------------------|-------------------------|
| `RoboSettings#isUseGlobalScheduler()`  | Use `LooperMode.PAUSED` |
| `RoboSettings#setUseGlobalScheduler()` | Use `LooperMode.PAUSED` |

### Removals

| Removed symbol                               | Replacement        |
|----------------------------------------------|--------------------|
| `RobolectricTestRunner#getConfig()`          | `Provider<Config>` |
| `RuntimeEnvironment#setUseLegacyResources()` | N/A                |

## Migrating to 4.0<a name="migrating-to-40"></a>

### Project Configuration

Robolectric 4.0 requires Android Gradle Plugin 3.2 or greater.

Update the configuration in your module's `build.gradle`/`build.gradle.kts` file:

/// tab | Groovy

```groovy
android {
  compileSdkVersion 28 // Or newer
  testOptions.unitTests.includeAndroidResources = true
}
```

///

/// tab | Kotlin

```kotlin
android {
  compileSdkVersion = 28 // Or newer
  testOptions.unitTests.isIncludeAndroidResources = true
}
```

///

Add the following in your `gradle.properties` file:

```properties
android.enableUnitTestBinaryResources=true
```

If you have dependencies on `com.android.support.test`, switch them to `androidx.test`; see
[Migrate to AndroidX][migrate-to-androidx].

### Deprecations

| 3.8                                        | 4.0                                                                                               |
|--------------------------------------------|---------------------------------------------------------------------------------------------------|
| `ShadowApplication.getInstance()`          | [`RuntimeEnvironment.application`][runtime-environment-application-javadoc]                       |
| `ShadowApplication.getLatestAlertDialog()` | [`ShadowAlertDialog.getLatestAlertDialog()`][shadow-alert-dialog-get-latest-alert-dialog-javadoc] |
| `ShadowApplication.getLatestDialog()`      | [`ShadowDialog.getLatestDialog()`][shadow-dialog-get-latest-dialog-javadoc]                       |
| `ShadowApplication.getLatestPopupMenu()`   | [`ShadowPopupMenu.getLatestPopupMenu()`][shadow-popup-menu-get-latest-popup-menu-javadoc]         |
| `ShadowLooper.getShadowMainLooper()`       | [`shadowOf(Looper.getMainLooper())`][shadow-of-looper-javadoc]                                    |

The [automatic migration tool](automated-migration.md) includes a migration to help with this.

The following attributes of the [`@Config`][config-javadoc] annotation are no longer supported when
using binary resources mode:

- [`assetDir`][config-asset-dir-javadoc] and [`resourceDir`][config-resource-dir-javadoc]: follow
  the recommended file structure of your build system.
- [`manifest`][config-manifest-javadoc]: Robolectric always uses the merged manifest generated by
  the Android toolchain. If your test was using a custom manifest you'll need to adapt it to not
  rely on that.
- [`packageName`][config-package-name-javadoc]: to change your package name, override the
  `applicationId` in your build system.

### Improper Use of Shadows

Prior to Robolectric 4.0, it was possible (but ill-advised) to get the shadow for an Android
framework object and invoke framework methods there. This could result in unexpected behavior (e.g.,
code in overridden methods in subclasses wouldn't be called). Shadow implementation methods are now
marked `protected` to guard against this. Always invoke framework methods directly on the Android
class.

| 3.8                                      | 4.0                                                                      |
|------------------------------------------|--------------------------------------------------------------------------|
| `shadowOf(activity).finish();`           | [`activity.finish()`][activity-finish-documentation]                     |
| `ShadowSystemClock.currentTimeMillis();` | [`System.currentTimeMillis()`][system-current-time-millis-documentation] |

The [automatic migration tool](automated-migration.md) will fix most of these for you.

### `androidx.test`

Robolectric 4.0 includes initial support for [`androidx.test` APIs][androidx-test-apis]. We strongly
recommend adding the latest version of `androidx.test:core` as a test dependency and using those
APIs whenever possible rather than using Robolectric-specific APIs.

| 3.8                              | 4.0                                                                                                         |
|----------------------------------|-------------------------------------------------------------------------------------------------------------|
| `RuntimeEnvironment.application` | [`ApplicationProvider.getApplicationContext()`][application-provider-get-application-context-documentation] |
| `ShadowMotionEvent`              | [`MotionEventBuilder`][motion-event-builder-documentation]                                                  |

### Troubleshooting

Robolectric 4.0 replaces its old home-grown resource handling code with a direct adaptation of
Android's resource handling code, using the full Android toolchain. This greatly improves fidelity
to the behavior of a real Android device, but if your tests were relying on the quirks of the old
code, you may need to fix your tests.

Some likely issues include:

!!! quote ""

    > android.view.InflateException: Binary XML file line #3: Failed to resolve attribute at index
    > 17: TypedValue{t=0x2/d=0x7f01000e a=-1}

    This happens when your [`Activity`][activity-documentation] is using a theme that lacks values
    for certain attributes used by layouts. Make sure you've specified an appropriate theme for your
    activities in your `AndroidManifest`.

[activity-documentation]: https://developer.android.com/reference/android/app/Activity
[activity-finish-documentation]: https://developer.android.com/reference/android/app/Activity#finish()
[androidx-test-apis]: https://developer.android.com/reference/androidx/test/package-summary
[application-provider-get-application-context-documentation]: https://developer.android.com/reference/androidx/test/core/app/ApplicationProvider#getApplicationContext()
[build-system-integration]: https://robolectric.org/build-system-integration
[config-asset-dir-javadoc]: javadoc/latest/org/robolectric/annotation/Config.html#assetDir()
[config-javadoc]: javadoc/latest/org/robolectric/annotation/Config.html
[config-manifest-javadoc]: javadoc/latest/org/robolectric/annotation/Config.html#manifest()
[config-package-name-javadoc]: javadoc/latest/org/robolectric/annotation/Config.html#packageName()
[config-resource-dir-javadoc]: javadoc/latest/org/robolectric/annotation/Config.html#resourceDir()
[migrate-to-androidx]: https://developer.android.com/jetpack/androidx/migrate
[motion-event-builder-documentation]: https://developer.android.com/reference/androidx/test/core/view/MotionEventBuilder
[runtime-environment-application-javadoc]: javadoc/latest/org/robolectric/RuntimeEnvironment.html#application
[shadow-alert-dialog-get-latest-alert-dialog-javadoc]: javadoc/latest/org/robolectric/shadows/ShadowAlertDialog.html#getLatestAlertDialog()
[shadow-dialog-get-latest-dialog-javadoc]: javadoc/latest/org/robolectric/shadows/ShadowDialog.html#getLatestDialog()
[shadow-of-looper-javadoc]: javadoc/latest/org/robolectric/Shadows.html#shadowOf(android.os.Looper)
[shadow-popup-menu-get-latest-popup-menu-javadoc]: javadoc/latest/org/robolectric/shadows/ShadowPopupMenu.html#getLatestPopupMenu()
[system-current-time-millis-documentation]: https://developer.android.com/reference/java/lang/System#currentTimeMillis()
