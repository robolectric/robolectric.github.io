---
title: Building Robolectric
group: Contributing
order: 1
toc: true
---

# Building Robolectric

This page describes how to set up a development environment to build Robolectric in the supported OSs (Linux, Mac, Windows).

JDK 11 is currently recommended to build Robolectric. Newer versions of the JDK (e.g. 17) will likely work, but may contain some rough edges.

## Installing Android SDK Tools

The first step is to install the Android SDK tools. The easiest way to do this is to install Android Studio, which also installs a copy of the
Android SDK tools, and provides the SDK Manager UI to manage SDK versions. Alternatively it is also possible to only download the Android command line tools without
installing the entire Android Studio. However, it is recommended to install Android Studio if possible. Visit [https://developer.android.com/studio#download](https://developer.android.com/studio#download) to get started.

Many of Robolectric's [integration tests](https://github.com/robolectric/robolectric/tree/master/integration_tests)
require the Android build tools to be installed and specific SDK versions to be installed.

## Linux

This section contains instructions for an Ubuntu Linux system. Other Linux systems will have different package management commands.

```
# Install prerequisites
sudo apt-get update
sudo apt-get install git default-jdk openjdk-11-jdk clang make cmake ninja-build
git clone --recurse-submodules https://github.com/robolectric/robolectric.git
# If you forgot --recurse-submodules, you can also run `git submodule update --init --recursive` from the 'robolectric' directory.
cd robolectric
./gradlew clean assemble
```

If you encounter `configure: error: Namespace support is required to build ICU` building error on Ubuntu 22.04 or other Linux variants, you can refer the issue: [nativeruntime/icu build failed on Ubuntu 22.04 with c++ namespace not supported error](https://github.com/robolectric/robolectric/issues/7977) to fix your Linux environments for building.

## Mac

### Install XCode command line tools

In order to get the C/C++ toolchain, you will need to install the XCode command line tools. To do this it is usually as simple as opening
a terminal and running `xcode-select --install`.

### Install JDK 11

By default Mac does not come with a JDK. It is  recommended to use [Azul](https://www.azul.com/downloads/?package=jdk), as they support the M1 architecture.

### Install homebrew

To build Robolectric on a Mac, [homebrew](https://brew.sh/) is required.

### Building

```
brew install cmake ninja
git clone --recurse-submodules https://github.com/robolectric/robolectric.git
cd robolectric
./gradlew clean assemble
```

## Windows

### Install JDK 11

By default Windows does not come with a JDK. It is  recommended to install [Adoptium Temurin 11](https://adoptium.net/?variant=openjdk11&jvmVariant=hotspot).

### Install msys2 64 bit

Install the 64 bit version of msys2. You can follow the instructions at https://www.msys2.org.

### Building

Open an msys2 terminal by running the "MSYS2 MINGW64" shortcut. This will ensure that `/mingw64/bin` is on the PATH.

```
pacman -Syu # Update system
pacman -Sy base-devel mingw-w64-x86_64-toolchain # Install the ming-w64-x86_64 package group
pacman -Sy git mingw-w64-x86_64-cmake mingw-w64-x86_64-ninja
git clone --recurse-submodules https://github.com/robolectric/robolectric.git
export ANDROID_SDK_ROOT=/c/Users/$USER/AppData/Local/Android/Sdk
export JAVA_HOME=/c/Program\ Files/Eclipse\ Adoptium/jdk-11.0.14.101-hotspot # Will likely be a different version on your machine
./gradlew clean assemble
```
