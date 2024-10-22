---
hide:
- toc
---

# Customizing the Test Runner

There are several situations where you want to customize Robolectric's test runner to perform some
operation before all tests are run, or even before each test method is run. One good example is
initializing a dependency injection framework with a different set of dependencies for your test.
Fortunately, Robolectric has a way to hook into the test lifecycle. If you define an
[`Application`][application-documentation] class in your `AndroidManifest.xml` file, Robolectric
will automatically try and load a test version of your `Application` class first.

## Hilt

If you're using Hilt, you can check the official
[Robolectric testing integration][dagger-hilt-robolectric] guide from Hilt.

[application-documentation]: https://developer.android.com/reference/android/app/Application
[dagger-hilt-robolectric]: https://dagger.dev/hilt/robolectric-testing.html
