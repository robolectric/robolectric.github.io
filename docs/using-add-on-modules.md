# Using add-on modules

In order to reduce the number of external dependencies on the application being tested, Robolectric's shadows are split into various add-on packages. Only shadows for classes provided in the base Android SDK are provided by the main Robolectric module. Additional shadows are provided in dedicated packages.

> [!NOTE]
> - [Robolectric 3.4](https://github.com/robolectric/robolectric/releases/tag/robolectric-3.4) doesn't include the `shadows-` prefix in the package name (i.e., `org.robolectric:playservices` and `org.robolectric:httpclient`).
> - Before Robolectric 3.4, `org.robolectric:playservices` was named `shadow-play-services`.

## Supported packages

| SDK package | Robolectric add-on package | Javadoc |
|-----|-----|-----|
| `androidx.multidex.MultiDex` | [`org.robolectric:shadows-multidex`](https://github.com/robolectric/robolectric/tree/robolectric-4.13/shadows/multidex) | [Javadoc](javadoc/latest/org/robolectric/shadows/multidex/package-summary.html) |
| `com.android.support.multidex` | [`org.robolectric:shadows-multidex`](https://github.com/robolectric/robolectric/tree/robolectric-4.13/shadows/multidex) | [Javadoc](javadoc/latest/org/robolectric/shadows/multidex/package-summary.html) |
| `com.google.android.gms:play-services` | [`org.robolectric:shadows-playservices`](https://github.com/robolectric/robolectric/tree/robolectric-4.13/shadows/playservices) | [Javadoc](javadoc/latest/org/robolectric/shadows/gms/package-summary.html) |

## Deprecated packages

| SDK package | Robolectric add-on package | Javadoc | Comment |
|-----|-----|-----|-----|
| `org.apache.httpcomponents:httpclient` | [`org.robolectric:shadows-httpclient`](https://github.com/robolectric/robolectric/tree/robolectric-4.13/shadows/httpclient) | [Javadoc](javadoc/latest/org/robolectric/shadows/httpclient/package-summary.html) | These shadows are only provided for legacy compatibility. |

## Removed packages

| SDK package | Robolectric add-on package | Comment |
|-----|-----|-----|
| `com.android.support.support-v4` | `org.robolectric:shadows-supportv4` | This package was deprecated in [Robolectric 4.8](https://github.com/robolectric/robolectric/releases/tag/robolectric-4.8), and removed in [Robolectric 4.9](https://github.com/robolectric/robolectric/releases/tag/robolectric-4.9). |
