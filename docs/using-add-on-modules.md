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

## Deprecated packages

| SDK package                            | Robolectric add-on package                                            | Javadoc                                 | Comment                                                                     |
|----------------------------------------|-----------------------------------------------------------------------|-----------------------------------------|-----------------------------------------------------------------------------|
| `com.google.android.gms:play-services` | [`org.robolectric:shadows-playservices`][shadows-playservices-source] | [Javadoc][shadows-playservices-javadoc] | This package was deprecated in [Robolectric 4.15][robolectric-4.15-release] |
| `org.apache.httpcomponents:httpclient` | [`org.robolectric:shadows-httpclient`][shadows-httpclient-source]     | [Javadoc][shadows-httpclient-javadoc]   | These shadows are only provided for legacy compatibility.                   |

## Removed packages

<!-- markdownlint-disable MD033 -->
| SDK package                      | Robolectric add-on package          | Comment                                                                                                                |
|----------------------------------|-------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| `androidx.multidex.MultiDex`     | `org.robolectric:shadows-multidex`  | Deprecated in [Robolectric 4.14][robolectric-4.14-release]<br/>Removed in [Robolectric 4.15][robolectric-4.15-release] |
| `com.android.support.multidex`   | `org.robolectric:shadows-multidex`  | Deprecated in [Robolectric 4.14][robolectric-4.14-release]<br/>Removed in [Robolectric 4.15][robolectric-4.15-release] |
| `com.android.support.support-v4` | `org.robolectric:shadows-supportv4` | Deprecated in [Robolectric 4.8][robolectric-4.8-release]<br/>Removed in [Robolectric 4.9][robolectric-4.9-release]     |

[robolectric-3.4-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-3.4
[robolectric-4.8-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-4.8
[robolectric-4.9-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-4.9
[robolectric-4.14-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-4.14
[robolectric-4.15-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-4.15
[shadows-httpclient-javadoc]: javadoc/latest/org/robolectric/shadows/httpclient/package-summary.html
[shadows-httpclient-source]: https://github.com/robolectric/robolectric/tree/master/shadows/httpclient
[shadows-playservices-javadoc]: javadoc/latest/org/robolectric/shadows/gms/package-summary.html
[shadows-playservices-source]: https://github.com/robolectric/robolectric/tree/master/shadows/playservices
