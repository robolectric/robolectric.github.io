---
title: Using Add-On Modules
group: User Guide
order: 4
---

# Using Add-On Modules

In order to reduce the number of external dependencies on the application being tested, Robolectric's shadows are split into various add-on packages. Only shadows for classes provided in the base Android SDK are provided by the main Robolectric module. Additional shadows for things like appcompat or the support library are provided by add-on modules. The table below lists the available add-on shadow packages:

| SDK Package                          | Robolectric Add-On Package            |
|--------------------------------------|---------------------------------------|
| com.android.support.support-v4       | org.robolectric:shadows-supportv4    |
| com.android.support.multidex         | org.robolectric:shadows-multidex      |
| com.google.android.gms:play-services | org.robolectric:shadows-playservices |
| org.apache.httpcomponents:httpclient | org.robolectric:shadows-httpclient    |

The above artifact names are in use since Robolectric 3.5+. Robolectric 3.4.x drops `shadows-` prefix from artifact names. Prior to 3.4, all artifact names are the same as latest, except that `shadows-supportv4` and `shadow-playservices` are `shadows-support-v4` and `shadow-play-services` respectively.

Note that `org.robolectric:shadows-supportv4` was deprecated at Robolectric 4.8, and will be removed at Robolectric 4.9.