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

Many of Robolectric's [integration tests](https://github.com/robolectric/robolectric/tree/master/integration_tests)
require the Android build tools to be installed and specific SDK versions to be installed.

## Linux

This section contains instructions for an Ubuntu Linux system. Other Linux systems will have different package management commands.

There are some tools that are used by Robolectric to download code, build code:

1. git.
2. OpenJDK 17.

**Note:** `openjdk-17-jdk` may need to be installed separately, as it's not accessible in all Ubuntu versions. Here's how you can install it:

If you're using Ubuntu 22.04+, you can run following commands to install these tools:

```
sudo apt install git openjdk-17-jdk
```

If you're using other Ubuntu or Linux variants you can search to find the proper approaches to install these tools.
Proceed with the rest of the Robolectric setup:
```
sudo apt-get install git
git clone https://github.com/robolectric/robolectric.git
cd robolectric
./gradlew clean assemble
```

## Mac

### Install XCode command line tools

In order to get the C/C++ toolchain, you will need to install the XCode command line tools. To do this it is usually as simple as opening
a terminal and running `xcode-select --install`.

### Install JDK 17

By default Mac does not come with a JDK. It is  recommended to use [Azul](https://www.azul.com/downloads/?package=jdk), as they support the M1 architecture.

### Install homebrew

To build Robolectric on a Mac, [homebrew](https://brew.sh/) is required.

### Building

```
git clone https://github.com/robolectric/robolectric.git
cd robolectric
./gradlew clean assemble
```

## Windows

### Install JDK 17

By default Windows does not come with a JDK. It is recommended to install the 64 bit version of [Adoptium Temurin 17](https://adoptium.net/?variant=openjdk17&jvmVariant=hotspot).

### Install msys2 64 bit

Install the 64 bit version of msys2. You can follow the instructions at https://www.msys2.org.

### Building

Open an msys2 terminal by running the "MSYS2 MINGW64" shortcut. This will ensure that `/mingw64/bin` is on the PATH.

```
pacman -Syu # Update system
pacman -Sy git # Install git
git clone https://github.com/robolectric/robolectric.git
export ANDROID_SDK_ROOT=/c/Users/$USER/AppData/Local/Android/Sdk
export JAVA_HOME=/c/Program\ Files/Eclipse\ Adoptium/jdk-17.0.10.7-hotspot # Will likely be a different version on your machine
./gradlew clean assemble
```
