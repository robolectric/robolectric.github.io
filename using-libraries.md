---
title: Using Library Resources
group: User Guide
order: 5
---

# Using Library Resources

When Robolectric runs a test, it attempts to load and index all resources provided by your application so it can return these resources when you call into the AssetManager. For resources provided by 3rd party libraries (i.e. aars or apklibs), some additional configuration is required.

### Using Libraries with Gradle

If you use Gradle to build your application and you are using `RobolectricTestRunner` to run your tests, no additional configuration is required. This is because the Android Gradle plugin will merge 3rd party library resources with your application's resources at build time. Note that the `RobolectricGradleTestRunner` test runner was [**deprecated**](https://github.com/robolectric/robolectric/wiki/3.0-to-3.1-Upgrade-Guide) in version 3.1.

### Using Libraries with Maven

If you use Maven to build your application, you will need to tell Robolectric where the unpacked resources are located for each library you use. This can either be specified in the `@Config` annotation:

```java
@RunWith(RobolectricTestRunner.class)
@Config(libraries = {
    "build/unpacked-libraries/library1",
    "build/unpacked-libraries/library2"
})
public class SandwichTest {
}
```

or specified in the `robolectric.properties` file:

```properties
libraries=build/unpacked-libraries/library1,build/unpacked-libraries/library2
```

All paths are relative to the root directory of the project.

### Debugging Resource Loading Issues

If you are not sure if resources are being loaded for a particular library, enable debug logging by setting the system property `robolectric.logging.enabled = true` and run your tests. You should see lots of output like:

```
Loading resources for 'com.foo' from build/unpacked-libraries/library1...
```

If you don't see a particular library in the list, double-check the configuration.