---
title: Other Resources
group: Contributing
order: 4
---

# Who's Using Robolectric?

* [Pivotal Labs](http://pivotallabs.com)
* [Square](https://squareup.com)
* [Path](http://www.path.com)
* [Zoodles](http://www.zoodles.com/home/marketing)
* [SoundCloud](https://market.android.com/details?id=com.soundcloud.android)
* [Found](http://beta.getfoundapp.com)
* [Frogtek](http://frogtek.org)
* [NASA Trained Monkeys](http://www.nasatrainedmonkeys.com)
* [Zauber](http://www.zaubersoftware.com/en/home)
* [RoboGuice](http://code.google.com/p/roboguice)
* [Android in Practice](http://code.google.com/p/android-in-practice)

## Presentation: TDD Android Applications with Robolectric

Pivotal Labs developers have given this [presentation](http://www.slideshare.net/joemoore1/tdd-android-applications-with-robolectric) several times.

## Troubleshooting

### java.lang.RuntimeException: Stub!

* Make sure you've put `@RunWith(RobolectricTestRunner.class)` at the top of your test class.
* Make sure that robolectric and its dependencies (including JUnit) appear before the Android API jars in the classpath.

### Could not resolve dependencies for project: Could not find artifact com.google.android.maps:maps:jar:18_r3 in central (http://repo1.maven.org/maven2)

The jerk lawyers at Google won't allow the Google maps add-on library stubs to be uploaded to Maven Central. You need
to manually install them yourself.

Make sure you've got the Android Google SDK listed [here](https://github.com/robolectric/robolectric/blob/master/pom.xml#L95)
(look for `com.google.android.maps`; currently it's `18_r3`) downloaded, then do this:

    ./script/install-maps-jar.sh

## Type com.google.android.maps.MapView not present

<div class="stacktrace">java.lang.TypeNotPresentException: Typecom.google.android.maps.MapView not present
       at org.robolectric.Robolectric.bindShadowClass(Robolectric.java:67)
Caused by: java.lang.ClassNotFoundException: caught an exception while obtaining a class file for com.google.android.maps.MapView
...
</div>

1. Make sure you have the Google Maps API jar in your build path.
2. Even if you're building against an earlier version of the API, link Robolectric to version 7 or higher.
