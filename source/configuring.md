---
title: Configuring Robolectric
group: User Guide
order: 2
---

# Configuring Robolectric

There are numerous ways to customize how Robolectric behaves at runtime.

## Config Annotation

The primary way to customize Robolectric is done via the `@Config` annotation. The annotation can be applied to classes and methods, with the values specified at the method level overriding the values specified at the class level.

Base classes are also searched for annotations, so if you find yourself specifying the same values on a large number of tests, you can create a base class and move your `@Config` annotation to that class.

The following examples show how to handle common setup tasks:

### Configure SDK Level

Robolectric will run your code against the `targetSdkVersion` specified in your manifest. If you want to test how specific pieces of code behave under a different SDK level, you can change the sdk version by setting:

```
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN)
public class SandwichTest {

    @Config(sdk = Build.VERSION_CODES.KITKAT)
    public void getSandwich_shouldReturnHamSandwich() {
    }
}
```

### Configure Application Class

Robolectric will attempt to create an instance of your Application class as specified in the manifest. If you want to provide a custom implementation, you can specify it by setting:

```
@Config(application = CustomApplication.class)
public class SandwichTest {

    @Config(application = CustomApplicationOverride.class)
    public void getSandwich_shouldReturnHamSandwich() {
    }
}
```

### Configure Resource Paths

Robolectric provides defaults for Gradle and Maven, but allows you to customize the path to your manifest, resource directory, and assets directory. This can be useful if you have a custom build system. You can specify these values by setting:

```
@Config(manifest = "some/build/path/AndroidManifest.xml")
public class SandwichTest {

    @Config(manifest = "other/build/path/AndroidManifest.xml")
    public void getSandwich_shouldReturnHamSandwich() {
    }
}
```

By default, Robolectric will assume that your resources and assets are located in directories named `res` and `assets`, respectively. These paths are assumed to be relative to the directory where the manifest is located. You can change these values by adding the `resourcesDir` and `assetsDir` options to the `@Config` annotaton.

## Config Properties

Any option that can be specified in a `@Config` annotation can also be specified globally in a properties file. Create a file named `robolectric.properties` and make sure it can be found on the classpath. Below is an example:

```
sdk=18
manifest=some/build/path/AndroidManifest.xml
shadows=my.package.ShadowFoo,my.package.ShadowBar
```

## System Properties

Some options can be configured globally by setting these system properties:

* **robolectric.offline** - Set to true to disable runtime fetching of jars.
* **robolectric.dependency.dir** - When in offline mode, specifies a folder containing runtime dependencies.
* **robolectric.dependency.repo.id** - Set the ID of the Maven repository to use for the runtime dependencies (default `sonatype`).
* **robolectric.dependency.repo.url** - Set the URL of the Maven repository to use for the runtime dependencies (default `https://oss.sonatype.org/content/groups/public/`).
* **robolectric.logging.enable** - Set to true to enable debug logging.

When using Gradle, you can configure the System Properties for unit tests with the `all` block (see [here](http://tools.android.com/tech-docs/unit-testing-support)). For example, to override the Maven repository URL and ID to download the runtime dependencies from a repository other than Sonatype:

	android {
	  testOptions {
	    unitTests.all {
	      systemProperty 'robolectric.dependency.repo.url', 'https://local-mirror/repo'
	      systemProperty 'robolectric.dependency.repo.id', 'local'
	    }
	  }
	}
