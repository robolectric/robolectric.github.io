---
title: Build System Integration
group: Customizing
order: 4
---

# Build System Integration

Starting with Robolectric 3.3, the test runner will look for a file named `/com/android/tools/test_config.properties` on the classpath. If it is found, it will be used to provide the default manifest, resource, and asset locations for tests, without the need to specify `@Config(constants=BuildConfig.class)` or `@Config(manifest="...", res="...", assets="...")` in your tests.

This gives build system implementors the ability to perform manifest and resource merging for tests using the same strategy it would when building the APK, rather than leaving it up to Robolectric.

This supersedes Robolectric's support for merging multiple libraries specified via `@Config(libraries = {"lib1", "lib2"})` and `project.properties` support from legacy Eclipse ADT projects.

Keys in the file:
* `android_sdk_home`: Full path to the installed Android SDK.
* `android_merged_manifest`: Full path to the project's merged `AndroidManifest.xml` file.
* `android_merged_resources`: Full path to the project's merged resources.
* `android_merged_assets`: Full path to the project's merged assets.

Note that Robolectric expects that build systems will have performed final `R.class` generation by the time unit tests are run.

Example:

```properties
android_sdk_home=/some/path/Android/Sdk/platforms/android-25
android_merged_assets=/some/path/MyApp/app/build/intermediates/assets/debug
android_merged_resources=/some/path/MyApp/app/build/intermediates/res/merged/debug
android_merged_manifest=/some/path/MyApp/app/build/intermediates/manifests/full/debug/AndroidManifest.xml
```
