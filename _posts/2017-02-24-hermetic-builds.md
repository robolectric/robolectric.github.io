---
layout: post
title:  "Hermetic Builds with Robolectric"
date:   2017-02-23 16:40:06 -0800
categories: build
hide: true
author: Christian Williams
---
Robolectric needs access to multiple Android SDK jars in order to perform its magic, which means it needs special configuration beyond just setting up dependencies in your build. By default, it tries to download Android SDK jars from Maven Central.

But what if you have a [hermetic build environment](http://blog.fahhem.com/2013/12/hermetic-build-systems/)? You just need to do a little more configuration.

Here's a [Gradle build script](https://gist.github.com/xian/05c4f27da6d4156b9827842217c2cd5c) that'll help.

1. Create an empty Gradle project (either `gradle init` or use Android Studio or IntelliJ).
1. Paste the script into your `build.gradle`.
1. Change the first line (`robolectricVersion`) to the version of Robolectric you want.
1. Run the `filesForHermeticBuild` task: `./gradlew filesForHermeticBuild`

Gradle will download all the dependencies you need to run Robolectric and place them in `build/output/libs`. Place the `.jar` files in your project's libs directory.

You'll also find a file called `build/output/robolectric-deps.properties`. Place it in your test resources directory. Change the paths as indicated in the comment in that file.

You're all set! Robolectric will now load Android SDKs from the filesystem instead of attempting to download them from Maven Central.