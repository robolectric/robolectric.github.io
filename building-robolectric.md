---
title: Building Robolectric
group: Contributing
order: 1
toc: true
---

# Building Robolectric

This page describes how to set up a development environment to build Robolectric in the supported OSs (Linux, Mac, Windows).

JDK 17 is currently required to build Robolectric. Newer versions of the JDK (e.g. 21) will likely work, but may contain some rough edges.

## Installing Android SDK Tools

The first step is to install the Android SDK tools. The easiest way to do this is to install Android Studio, which also installs a copy of the
Android SDK tools, and provides the SDK Manager UI to manage SDK versions. Alternatively it is also possible to only download the Android command line tools without
installing the entire Android Studio. However, it is recommended to install Android Studio if possible. Visit [https://developer.android.com/studio#download](https://developer.android.com/studio#download) to get started.

If you install the Android Studio, it's recommended to use the latest stable Android Studio because Robolectric
keeps using the latest stable AGP, and it requires a recent Android Studio.

Many of Robolectric's [integration tests](https://github.com/robolectric/robolectric/tree/master/integration_tests)
require the Android build tools to be installed and specific SDK versions to be installed.

## Install Git and OpenJDK 17

1. Install git to download Robolectric source code. See [git-scm download](https://git-scm.com/downloads) to get started.
2. Install OpenJDK 17 to build and run Robolectric tests. See [GitHub Action setup-java](https://github.com/actions/setup-java)
   to get the recommended OpenJDK distribution list. Any distribution that support JDK 17 is recommended. Different operating
   system has different OpenJDK installation and configuration tutorials, please search the internet to learn how to do it.

## Download source code

```shell
git clone https://github.com/robolectric/robolectric.git
```

## Building

### Building source code
```shell
./gradlew clean assemble
```

### Run local tests

```shell
./gradlew test --parallel -D"robolectric.enabledSdks=26,27,28"
```

### Run instrumentation tests with Android device

```shell
./gradlew cAT --info
```

If you're using Windows, it's recommended to use [PowerShell](https://github.com/PowerShell/PowerShell) in
[Windows Terminal](https://github.com/microsoft/terminal).