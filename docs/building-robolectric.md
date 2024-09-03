# Building Robolectric

This page describes how to set up a development environment to build and test Robolectric on the supported OSs (Linux, Mac, Windows).

## Installing Android SDK Tools

This can be achieved by either [installing Android Studio](https://developer.android.com/studio#download) (recommended),
or Android's [command line tools](https://developer.android.com/studio#command-line-tools-only).
We recommend using the latest stable release of Android Studio, because Robolectric uses the latest stable version of the
Android Gradle Plugin.

Robolectric's [integration tests](https://github.com/robolectric/robolectric/tree/master/integration_tests)
require Android Build Tools to be installed and specific SDK versions to be installed. Please check the relevant
modules to know which versions to install.

## Install Git and OpenJDK 17

JDK 17 is currently required to build Robolectric. Newer versions of the JDK (e.g. 21) will likely work, but may contain some rough edges.

1. [Install Git](https://git-scm.com/downloads) to download Robolectric source code.
2. Install OpenJDK 17 to build and test Robolectric. See [GitHub Action setup-java](https://github.com/actions/setup-java#supported-distributions)
   to get the recommended OpenJDK distribution list. Any distribution that supports JDK 17 is recommended. Different operating
   systems have different OpenJDK installation and configuration tutorials, please search the internet to learn how to do it.

## Download source code

Robolectric's source code is available on [GitHub](https://github.com/robolectric/robolectric). You can get by running the following command:

```shell
git clone git@github.com:robolectric/robolectric.git
```

## Building and testing

### Build Robolectric

Robolectric supports running tests against multiple Android API levels. To build Robolectric, run:

```shell
./gradlew clean assemble testClasses --parallel
```

### Run unit tests

```shell
./gradlew test --parallel
```

You can use the `robolectric.enabledSdks` argument to specify a subset of comma-separated supported
API levels to test:

```shell
# Replace 32,33,34 with the API levels you want to test
./gradlew test --parallel -"Drobolectric.enabledSdks=32,33,34"
```

### Run instrumentation tests

You can run the compatibility test suites on your connected devices (either a physical device or an
emulator) by running:

```shell
./gradlew connectedAndroidTest
```

If you're using Windows, it's recommended to use [PowerShell](https://github.com/PowerShell/PowerShell) in
[Windows Terminal](https://github.com/microsoft/terminal).

## Next step

Once you're up and running, you can have a look at the [architecture](https://github.com/robolectric/robolectric/blob/master/ARCHITECTURE.md) documentation
to learn more about Robolectric's components.
