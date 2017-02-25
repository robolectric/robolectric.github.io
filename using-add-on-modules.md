---
title: Using Add-On Modules
group: User Guide
order: 4
---

# Using Add-On Modules

In order to reduce the number of external dependencies on the application being tested, Robolectric's shadows are split into various add-on packages. Only shadows for classes provided in the base Android SDK are provided by the main Robolectric module. Additional shadows for things like appcompat or the support library are provided by add-on modules. The table below lists the available add-on shadow packages:

| SDK Package                          | Robolectric Add-On Package            |
|--------------------------------------|---------------------------------------|
| com.android.support.support-v4       | org.robolectric:shadows-support-v4    |
| com.android.support.multidex         | org.robolectric:shadows-multidex      |
| com.google.android.gms:play-services | org.robolectric:shadows-play-services |
| com.google.android.maps:maps         | org.robolectric:shadows-maps          |
| org.apache.httpcomponents:httpclient | org.robolectric:shadows-httpclient    |

Note that the add-on packages need to be specified in your `build.gradle` or `pom.xml` in addition to the Robolectric dependency.
