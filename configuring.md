---
title: Configuring Robolectric
group: User Guide
order: 2
---

# Configuring Robolectric

Several aspects of Robolectric's behavior can be configured at runtime, using `robolectric.properties` files for package-level configuration, or `@Config` annotations for class-level or method-level configuration.

### `@Config` Annotation

To configure Robolectric for a single test class or method, use the <a href="/javadoc/latest/org/robolectric/annotation/Config.html">`@Config`</a> annotation. The annotation can be applied to classes and methods; values specified at the method level will override values specified at the class level.

Base classes are also searched for annotations, so if you find yourself specifying the same values on a large number of tests, you can create a base class and move your `@Config` annotation to that class.

```java
  @Config(sdk=JELLYBEAN_MR1,
      manifest="some/build/path/AndroidManifest.xml",
      shadows={ShadowFoo.class, ShadowBar.class})
  public class SandwichTest {
  }
```

### `robolectric.properties` File

To configure all Robolectric tests within a package or group of packages, create a file named `robolectric.properties` in the appropriate package. Generally, this file would be placed within the appropriate package directory under `src/test/resources` in your project tree. Robolectric will search for properties files up the hierarchy of packages (including the unnamed default package at the top level), with values in deeper packages overriding values in more shallow packages. When test classes or methods have `@Config` annotations, those override any config from properties files.

Below is an example:

```properties
# src/test/resources/com/mycompany/app/robolectric.properties
sdk=18
manifest=some/build/path/AndroidManifest.xml
shadows=my.package.ShadowFoo,my.package.ShadowBar
```

***Version note:*** *Prior to Robolectric 3.1.3, only a top-level `robolectric.properties` file may be specified.* 

### Global Configuration

If you wish to change the default for any configurable value for all your tests, you may extend `RobolectricTestRunner` and override the `buildGlobalConfig()` method, then specify your custom test runner using the `@RunWith` annotation.

## Configurables

The following examples show how to handle common configuration tasks. For clarity, `@Config` annotations are used, but any of these values may also be configured using properties files.

### Configure SDK Level

By default, Robolectric will run your code against the `targetSdkVersion` specified in your manifest. If you want to test your code under a different SDK, you can specify the SDK using the `sdk`, `minSdk` and `maxSdk` config properties:

```java
@Config(sdk = { JELLY_BEAN, JELLY_BEAN_MR1 })
public class SandwichTest {

    public void getSandwich_shouldReturnHamSandwich() {
      // will run on JELLY_BEAN and JELLY_BEAN_MR1
    }

    @Config(sdk = KITKAT)
    public void onKitKat_getSandwich_shouldReturnChocolateWaferSandwich() {
      // will run on KITKAT
    }
    
    @Config(minSdk=LOLLIPOP)
    public void fromLollipopOn_getSandwich_shouldReturnTunaSandwich() {
      // will run on LOLLIPOP, M, etc.
    }
}
```

Note that `sdk` and `minSdk`/`maxSdk` may not be specified in the same config annotation or file; however, `minSdk` and `maxSdk` may be specified together. If any of them is present, they override any SDK specification from a less-specific configuration location.

***Version note:*** *Prior to Robolectric 3.2, `minSdk` and `maxSdk` are ignored, and `NEWEST`, `OLDEST`, and `TARGET` are not supported. Also, only integers corresponding to API levels may be specified in a properties file.* 

### Configure Application Class

Robolectric will attempt to create an instance of your Application class as specified in the manifest. If you want to provide a custom implementation, you can specify it by setting:

```java
@Config(application = CustomApplication.class)
public class SandwichTest {

    @Config(application = CustomApplicationOverride.class)
    public void getSandwich_shouldReturnHamSandwich() {
    }
}
```

### Configure Resource and Asset Paths

Robolectric provides defaults for Gradle and Maven, but allows you to customize the path to your manifest, resource directory, and assets directory. This can be useful if you have a custom build system. You can specify these values by setting:

```java
@Config(resourceDir = "some/build/path/res")
public class SandwichTest {

    @Config(resourceDir = "other/build/path/ham-sandwich/res")
    public void getSandwich_shouldReturnHamSandwich() {
    }
}
```

By default, Robolectric will assume that your resources and assets are located in directories named `res` and `assets`, respectively. These paths are assumed to be relative to the directory where the manifest is located. You can change these values by adding the `resourceDir` and `assetDir` options to the `@Config` annotaton.

### Configure Qualifiers

You can explicitly configure the set of resource qualifiers in effect for a test:

```java
public class SandwichTest {

    @Config(qualifiers = "fr-xlarge")
    public void getSandwichName() {
      assertThat(sandwich.getName()).isEqualTo("Grande Croque Monégasque");
    }
}
```

See [Using Qualifiers](/using-qualifiers) for more details.

## System Properties

Some additional options can be configured globally by setting these system properties:

* **robolectric.enabledSdks** — Comma-separated list of SDK levels or names (e.g. `19, 21` or `KITKAT, LOLLIPOP`) which are enabled for this process. Only tests targetted at the listed SDKs will be run. By default, all SDKs are enabled.
* **robolectric.offline** — Set to true to disable runtime fetching of jars.
* **robolectric.dependency.dir** — When in offline mode, specifies a folder containing runtime dependencies.
* **robolectric.dependency.repo.id** — Set the ID of the Maven repository to use for the runtime dependencies (default `sonatype`).
* **robolectric.dependency.repo.url** — Set the URL of the Maven repository to use for the runtime dependencies (default `https://oss.sonatype.org/content/groups/public/`).
* **robolectric.logging.enabled** — Set to true to enable debug logging.

When using Gradle, you can configure the System Properties for unit tests with the `all` block (see [here](http://tools.android.com/tech-docs/unit-testing-support)). For example, to override the Maven repository URL and ID to download the runtime dependencies from a repository other than Sonatype:

```groovy
android {
  testOptions {
    unitTests.all {
      systemProperty 'robolectric.dependency.repo.url', 'https://local-mirror/repo'
      systemProperty 'robolectric.dependency.repo.id', 'local'
    }
  }
}
```