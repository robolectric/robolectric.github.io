---
layout: default
title: Customizing the Test Runner
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

If you're using RoboGuice, you would initialize the injector in your Application class:

```java
public class FooApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ApplicationModule module = new ApplicationModule();
        setBaseApplicationInjector(this, DEFAULT_STAGE, newDefaultRoboModule(this), module);
    }
}
```

You can define a test version of the application named TestFooApplication:

```java
public class TestFooApplication extends FooApplication implements TestLifecycleApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        TestApplicationModule module = new TestApplicationModule();
        setBaseApplicationInjector(this, DEFAULT_STAGE, newDefaultRoboModule(this), module);
    }

    @Override
    public void beforeTest(Method method) {
    }

    @Override
    public void prepareTest(Object test) {
        getInjector(this).injectMembers(test);
    }

    @Override
    public void afterTest(Method method) {
    }
}
```

Robolectric will load the test version of the application which you can use to load a different set of bindings
during tests.
