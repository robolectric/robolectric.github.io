---
title: Customizing the Test Runner
group: Customizing
order: 3
---

# Customizing the Test Runner

There are several situations where you want to customize Robolectric's test runner to perform some operation
before all tests are run, or even before each test method is run. One good example is initializing a dependency
injection framework with a different set of dependencies for your test. Fortunately, Robolectric has a way to
hook into the test lifecycle. If you define an Application class in your AndroidManifest.xml, Robolectric will
automatically try and load a test version of your application class first. For example:

Let's say you've defined a FooApplication in your manifest:

```xml
<application android:name=".FooApplication">
```

## Hilt

If you're using Hilt, you can check the official [Robolectric testing integration](https://dagger.dev/hilt/robolectric-testing.html) guide from Hilt.
