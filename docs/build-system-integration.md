---
hide:
- toc
---

# Build System Integration

Starting with [Robolectric 3.3](https://github.com/robolectric/robolectric/releases/tag/robolectric-3.3), the test runner will look for a file named `/com/android/tools/test_config.properties` on the classpath. If it is found, it will be used to provide the default manifest, resource, and asset locations for tests, without the need to specify `@Config(manifest = "...", resourceDir = "...", assetDir = "...")` in your tests.

This gives build system implementors the ability to perform manifest, asset and resource preprocessing and merging for tests using the same strategy it would when building the APK, rather than leaving it up to Robolectric.

This supersedes Robolectric's support for merging multiple libraries specified via `@Config(libraries = {"lib1", "lib2"})`.

## Supported keys

* `android_merged_manifest`: full path to the project's merged `AndroidManifest.xml` file.
* `android_merged_resources`: full path to the project's merged resources.
* `android_merged_assets`: full path to the project's merged assets.
* `android_custom_package`: Java package name for the applications `R` class.
* `android_resource_apk`: path to a `resources.ap_` file that contains binary resources and XML files produced by the `aapt` tool, as well as merged assets.

> [!NOTE]
> Robolectric expects that build systems have generated the final `R.class` file by the time unit tests are run.

For merged raw resources support:

```properties
android_merged_assets=/some/path/to/app/build/intermediates/assets/debug
android_merged_resources=/some/path/to/app/build/intermediates/merged_res/debug
android_merged_manifest=/some/path/to/app/build/intermediates/merged_manifests/debug/processDebugManifest/AndroidManifest.xml
android_custom_package=com.example.app
```

For binary resources support:

```properties
android_resource_apk=/some/path/to/app/resources.ap_
```
