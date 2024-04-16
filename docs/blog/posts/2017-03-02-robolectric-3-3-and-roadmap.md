---
date: 2017-03-02
authors:
  - xian
  - jongerrish
hide:
  - footer
slug: robolectric-3-3-and-roadmap
---

# Robolectric 3.3 and Roadmap

Your Robolectric maintainers are pleased to announce the release of [Robolectric 3.3](https://github.com/robolectric/robolectric/releases/tag/robolectric-3.3)! There's been a bunch of activity recently in Robolectric, and we wanted to give a quick update on our thinking about where the project is going.

<!-- more -->

### Introduction

Robolectric started life in 2010 as a quick hack to allow Android tests to be run on a regular JVM rather than a device or emulator, allowing for fast [TDD cycles](https://en.wikipedia.org/wiki/Test-driven_development#Test-driven_development_cycle). It was mostly developed in brief spurts as needed for testing specific projects.

Since then, it's been cool to see Robolectric grow with the help of the community, and become quite widely used. It's become a critical part of the test infrastructure of lots of companies and projects. It's rather outgrown the scrappy side-project Robolectric was for much of its life and needs some full-time attention.

Here's a high-level view of what we see as the steps ahead toward making Robolectric a trusted, well-supported and dependable Android test framework. In the coming months we’ll be working on:

### Android Fidelity and Trustworthiness
Long ago, Robolectric lurked mostly in the shadows: there was no real Android code behind the scenes, only the bits we implemented (which we called Shadows). From 2.0 on, we've had real Android implementations available to use, but lots of old shadow implementations lingered and gave peculiar behavior, plus some bits remained unimplemented or were implemented with behavior that doesn't match that of a device. All this has left Robolectric's trustworthiness in doubt. We're working to match Android's behavior as closely as possible in the spots where we can, and to provide predictable behavior in those places where it's necessary to diverge.

### API Stability
We've had a bit of API thrash and regressions in the past and we're going to work diligently to ease the pain of staying current with robolectric releases. We'll be deprecating APIs before their removal and providing guidance on migration. We do expect a few more large and high-benefit API changes in the coming major releases, but we'll do our best to minimize their impact. We'll be careful to document changes in release notes.

### Documentation
Robolectric’s documentation has always been sparse. We're aiming for clear and comprehensive documentation that reveals how the various parts of the Android SDK are simulated by Robolectric, and details extended APIs for testing.

### Performance
Robolectric was created to make TDD super fast, so it's disappointing to us too that performance has dipped. We're going to set up a perf suite and spend some time in a profiler and try to squeeze every second out of test startup and run time.

### Toolchain Integration
We're working on easier project integration for several build systems, tighter integration with Android Studio, and easier and more flexible configuration in general.

### Android SDK Support
As new SDKs and new features are released, we'll make sure Robolectric supports them quickly.

### Community
We're going to do our best to stay on top of bug reports and pull requests. We'll be continuing to develop in the open on GitHub, and we'd love your input on changes and designs.

As always, thanks for your pull requests, bug reports, ideas and questions!

_Your Robolectric maintainers,_
<br/>
[jongerrish@google.com](mailto:jongerrish@google.com) and [christianw@google.com](mailto:christianw@google.com)