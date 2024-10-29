# Using add-on modules

In order to reduce the number of external dependencies on the application being tested,
Robolectric's shadows are split into various add-on packages. Only shadows for classes provided in
the base Android SDK are provided by the main Robolectric module. Additional shadows are provided in
dedicated packages.

> [!NOTE]
>
> - [Robolectric 3.4][robolectric-3.4-release] doesn't include the `shadows-` prefix in the package
    name (i.e., `org.robolectric:playservices` and `org.robolectric:httpclient`).
> - Before Robolectric 3.4, `org.robolectric:playservices` was named `shadow-play-services`.

## Supported packages

| SDK package                            | Robolectric add-on package                                            | Javadoc                                 |
|----------------------------------------|-----------------------------------------------------------------------|-----------------------------------------|
| `androidx.multidex.MultiDex`           | [`org.robolectric:shadows-multidex`][shadows-multidex-source]         | [Javadoc][shadows-multidex-javadoc]     |
| `com.android.support.multidex`         | [`org.robolectric:shadows-multidex`][shadows-multidex-source]         | [Javadoc][shadows-multidex-javadoc]     |
| `com.google.android.gms:play-services` | [`org.robolectric:shadows-playservices`][shadows-playservices-source] | [Javadoc][shadows-playservices-javadoc] |

## Deprecated packages

| SDK package                            | Robolectric add-on package                                        | Javadoc                               | Comment                                                   |
|----------------------------------------|-------------------------------------------------------------------|---------------------------------------|-----------------------------------------------------------|
| `org.apache.httpcomponents:httpclient` | [`org.robolectric:shadows-httpclient`][shadows-httpclient-source] | [Javadoc][shadows-httpclient-javadoc] | These shadows are only provided for legacy compatibility. |

## Removed packages

| SDK package                      | Robolectric add-on package          | Comment                                                                                                                               |
|----------------------------------|-------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| `com.android.support.support-v4` | `org.robolectric:shadows-supportv4` | This package was deprecated in [Robolectric 4.8][robolectric-4.8-release], and removed in [Robolectric 4.9][robolectric-4.9-release]. |

[robolectric-3.4-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-3.4
[robolectric-4.8-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-4.8
[robolectric-4.9-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-4.9
[shadows-httpclient-javadoc]: javadoc/latest/org/robolectric/shadows/httpclient/package-summary.html
[shadows-httpclient-source]: https://github.com/robolectric/robolectric/tree/master/shadows/httpclient
[shadows-multidex-javadoc]: javadoc/latest/org/robolectric/shadows/multidex/package-summary.html
[shadows-multidex-source]: https://github.com/robolectric/robolectric/tree/master/shadows/multidex
[shadows-playservices-javadoc]: javadoc/latest/org/robolectric/shadows/gms/package-summary.html
[shadows-playservices-source]: https://github.com/robolectric/robolectric/tree/master/shadows/playservices
