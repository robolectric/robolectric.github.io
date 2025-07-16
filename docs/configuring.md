# Configuring Robolectric

Several aspects of Robolectric's behavior can be configured at runtime, using either
[`@Config` annotations](#config-annotation) for class- or method-level configuration, or
[`robolectric.properties` files](#robolectricproperties-file) for package-level configuration.

## Configuration methods

### `@Config` annotation

To configure Robolectric for a single test class or method, use the
[`@Config`][config-documentation] annotation on the desired class or method. Annotations applied on
methods take precedence over the ones at the class level.

Base classes are also searched for annotations. So if you find yourself specifying the same values
on a large number of tests, you can create a base class and move your `@Config` annotation to that
class.

/// tab | Java

```java
@Config(
  sdk = Build.VERSION_CODES.TIRAMISU,
  shadows = {ShadowFoo.class, ShadowBar.class}
)
public class SandwichTest {
}
```

///

/// tab | Kotlin

```kotlin
@Config(
  sdk = Build.VERSION_CODES.TIRAMISU,
  shadows = [ShadowFoo::class, ShadowBar::class]
)
class SandwichTest
```

///

### `robolectric.properties` file

To configure all Robolectric tests within a package or group of packages, create a file named
`robolectric.properties` in the appropriate package. Generally, this file would be placed within the
appropriate package directory under `src/test/resources` in your project tree. Robolectric will
search for properties files up the hierarchy of packages (including the unnamed default package at
the top level), with values in deeper packages overriding values in more shallow packages. When test
classes or methods have `@Config` annotations, those override any config from properties files.

Below is an example:

```properties title="src/test/resources/com/mycompany/app/robolectric.properties"
sdk=33
shadows=com.mycompany.ShadowFoo,com.mycompany.ShadowBar
```

> [!NOTE]
> Prior to [Robolectric 3.1.3][robolectric-3.1.3-release], only a top-level `robolectric.properties`
> file may be specified.

### Global Configuration

If you wish to change the default for any configurable value for all your tests, you can provide a
[`GlobalConfigProvider`][global-config-provider] service implementation.

## Configurables

The following examples show how to handle common configuration tasks. For clarity, `@Config`
annotations are used, but any of these values may also be configured using properties files.

### Configure SDK Level

By default, Robolectric will run your code against the `targetSdk` specified in your module's
`build.gradle`/`build.gradle.kts` or `AndroidManifest.xml` file. If you want to test your code under
a different SDK, you can specify the desired SDK(s) using the [`sdk`][config-sdk],
[`minSdk`][config-min-sdk] and [`maxSdk`][config-max-sdk] config properties:

/// tab | Java

```java
@Config(sdk = {TIRAMISU, UPSIDE_DOWN_CAKE})
public class SandwichTest {
  @Test
  public void getSandwich_shouldReturnHamSandwich() {
    // will run on TIRAMISU and UPSIDE_DOWN_CAKE
  }

  @Test
  @Config(sdk = TIRAMISU)
  public void onTiramisu_getSandwich_shouldReturnChocolateWaferSandwich() {
    // will run on TIRAMISU
  }

  @Test
  @Config(minSdk = UPSIDE_DOWN_CAKE)
  public void fromUpsideDownCakeOn_getSandwich_shouldReturnTunaSandwich() {
    // will run on UPSIDE_DOWN_CAKE, VANILLA_ICE_CREAM, etc.
  }
}
```

///

/// tab | Kotlin

```kotlin
@Config(sdk = [TIRAMISU, UPSIDE_DOWN_CAKE])
class SandwichTest {
  @Test
  fun getSandwich_shouldReturnHamSandwich() {
    // will run on TIRAMISU and UPSIDE_DOWN_CAKE
  }

  @Test
  @Config(sdk = TIRAMISU)
  fun onTiramisu_getSandwich_shouldReturnChocolateWaferSandwich() {
    // will run on TIRAMISU
  }

  @Test
  @Config(minSdk = UPSIDE_DOWN_CAKE)
  fun fromUpsideDownCakeOn_getSandwich_shouldReturnTunaSandwich() {
    // will run on UPSIDE_DOWN_CAKE, VANILLA_ICE_CREAM, etc.
  }
}
```

///

Note that `sdk` and `minSdk`/`maxSdk` may not be specified in the same `@Config` annotation or file;
however, `minSdk` and `maxSdk` may be specified together. If any of them is present, they override
any SDK specification from a less-specific configuration location.

> [!NOTE]
> Prior to [Robolectric 3.2][robolectric-3.2-release], `minSdk` and `maxSdk` are ignored, and
> [`NEWEST_SDK`][config-newest-sdk], [`OLDEST_SDK`][config-oldest-sdk], and
> [`TARGET_SDK`][config-target-sdk] are not supported. Also, only integers corresponding to API
> levels may be specified in a properties file.

### Configure `Application` class

Robolectric will attempt to create an instance of your [`Application`][application-documentation]
class as specified in the `AndroidManifest`. If you want to provide a custom implementation, you can
specify it by setting:

/// tab | Java

```java
@Config(application = CustomApplication.class)
public class SandwichTest {
  @Test
  @Config(application = CustomApplicationOverride.class)
  public void getSandwich_shouldReturnHamSandwich() {
  }
}
```

///

/// tab | Kotlin

```kotlin
@Config(application = CustomApplication::class)
class SandwichTest {
  @Test
  @Config(application = CustomApplicationOverride::class)
  fun getSandwich_shouldReturnHamSandwich() {
  }
}
```

///

### Configure qualifiers

You can explicitly configure the set of resource qualifiers in effect for a test:

/// tab | Java

```java
public class SandwichTest {
  @Test
  @Config(qualifiers = "fr-xlarge")
  public void getSandwichName() {
    assertThat(sandwich.getName()).isEqualTo("Grande Croque Monégasque");
  }
}
```

///

/// tab | Kotlin

```kotlin
class SandwichTest {
  @Test
  @Config(qualifiers = "fr-xlarge")
  fun getSandwichName() {
    assertThat(sandwich.name).isEqualTo("Grande Croque Monégasque")
  }
}
```

///

See [Using Qualified Resources](using-qualifiers.md) for more details.

## System properties

Some additional options can be configured globally by setting these system properties:

| Property name                          | Description                                                                                                                                                                                                            | Default value                    |
|----------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------|
| `robolectric.enabledSdks`              | Comma-separated list of SDK levels or names (e.g. `33, 34` or `TIRAMISU, UPSIDE_DOWN_CAKE`) which are enabled for this process. Only tests targeting a listed SDKs will be run.                                        | All SDKs                         |
| `robolectric.offline`                  | Set to `true` to disable runtime fetching of jars.                                                                                                                                                                     | `false`                          |
| `robolectric.usePreinstrumentedJars`   | If `true`, Robolectric will use instrumented jars to reduce instrumentation overhead. If changes are made to instrumentation, this can be set to `false` to ensure the changes are included when building Robolectric. | `true`                           |
| `robolectric.dependency.dir`           | When in offline mode, specifies a folder containing runtime dependencies.                                                                                                                                              | `null`                           |
| `robolectric.dependency.repo.id`       | Set the ID of the Maven repository to use for the runtime dependencies.                                                                                                                                                | `mavenCentral`                   |
| `robolectric.dependency.repo.url`      | Set the URL of the Maven repository to use for the runtime dependencies.                                                                                                                                               | `https://repo1.maven.org/maven2` |
| `robolectric.dependency.repo.username` | Username of the repository that you defined in `robolectric.dependency.repo.url`.                                                                                                                                      | `null`                           |
| `robolectric.dependency.repo.password` | Password of the repository that you defined in `robolectric.dependency.repo.url`.                                                                                                                                      | `null`                           |
| `robolectric.logging.enabled`          | Set to `true` to enable debug logging.                                                                                                                                                                                 | `false`                          |

Since [Robolectric 4.9.1][robolectric-4.9.1-release], you can now add these parameters:

| Property name                       | Description                                                           | Default value |
|-------------------------------------|-----------------------------------------------------------------------|---------------|
| `robolectric.dependency.proxy.host` | Set the host of the proxy to use for the runtime dependencies.        | `null`        |
| `robolectric.dependency.proxy.port` | Set the port number of the proxy to use for the runtime dependencies. | `0`           |

When using Gradle, you can configure the System Properties for unit tests with the
`android.testOptions.unitTests.all` block (see [here][configure-gradle-test-options]). For example,
to override the Maven repository URL and ID to download the runtime dependencies from a repository
other than Maven Central:

/// tab | Groovy

```groovy
android {
  testOptions {
    unitTests.all {
      systemProperty "robolectric.dependency.repo.url", "https://local-mirror/repo"
      systemProperty "robolectric.dependency.repo.id", "local"

      // Username and password only needed when the local repository needs account information.
      systemProperty "robolectric.dependency.repo.username", "username"
      systemProperty "robolectric.dependency.repo.password", "password"

      // Since Robolectric 4.9.1, these are available
      systemProperty "robolectric.dependency.proxy.host", project.findProperty("systemProp.https.proxyHost") ?: System.getenv("ROBOLECTRIC_PROXY_HOST")
      systemProperty "robolectric.dependency.proxy.port", project.findProperty("systemProp.https.proxyPort") ?: System.getenv("ROBOLECTRIC_PROXY_PORT")
    }
  }
}
```

///

/// tab | Kotlin

```kotlin
android {
  testOptions {
    unitTests.all {
      it.systemProperty("robolectric.dependency.repo.url", "https://local-mirror/repo")
      it.systemProperty("robolectric.dependency.repo.id", "local")

      // Username and password only needed when the local repository needs account information.
      it.systemProperty("robolectric.dependency.repo.username", "username")
      it.systemProperty("robolectric.dependency.repo.password", "password")

      // Since Robolectric 4.9.1, these are available
      it.systemProperty("robolectric.dependency.proxy.host", project.findProperty("systemProp.https.proxyHost") ?: System.getenv("ROBOLECTRIC_PROXY_HOST"))
      it.systemProperty("robolectric.dependency.proxy.port", project.findProperty("systemProp.https.proxyPort") ?: System.getenv("ROBOLECTRIC_PROXY_PORT"))
    }
  }
}
```

///

## `ConscryptMode`

Starting with [Robolectric 4.9][robolectric-4.9-release], Robolectric can either use Conscrypt and
BouncyCastle or just BouncyCastle as the security provider. In order to migrate tests over time,
there is a [`ConscryptMode`][conscrypt-mode] annotation that controls whether Conscrypt is loaded as
the default security provider with BouncyCastle as backup.

- If [`ConscryptMode.Mode`][conscrypt-mode-mode] is [`ON`][conscrypt-mode-mode-on], it will install
  Conscrypt and BouncyCastle.
- If [`ConscryptMode.Mode`][conscrypt-mode-mode] is [`OFF`][conscrypt-mode-mode-off], it will only
  install BouncyCastle.

This is closer to the way that it works on [real android][android-security]. Robolectric will search
for a requested security primitive from Conscrypt first. If it does not support it, Robolectric will
try BouncyCastle second.

[android-security]: https://cs.android.com/android/platform/superproject/+/android-13.0.0_r1:libcore/ojluni/src/main/java/java/security/Security.java;l=134-137
[application-documentation]: https://developer.android.com/reference/android/app/Application
[config-documentation]: javadoc/latest/org/robolectric/annotation/Config.html
[config-max-sdk]: javadoc/latest/org/robolectric/annotation/Config.html#maxSdk()
[config-min-sdk]: javadoc/latest/org/robolectric/annotation/Config.html#minSdk()
[config-newest-sdk]: javadoc/latest/org/robolectric/annotation/Config.html#NEWEST_SDK
[config-oldest-sdk]: javadoc/latest/org/robolectric/annotation/Config.html#OLDEST_SDK
[config-target-sdk]: javadoc/latest/org/robolectric/annotation/Config.html#TARGET_SDK
[config-sdk]: javadoc/latest/org/robolectric/annotation/Config.html#sdk()
[configure-gradle-test-options]: https://developer.android.com/studio/test/advanced-test-setup#configure-gradle-test-options
[conscrypt-mode]: javadoc/latest/org/robolectric/annotation/ConscryptMode.html
[conscrypt-mode-mode]: javadoc/latest/org/robolectric/annotation/ConscryptMode.Mode.html
[conscrypt-mode-mode-off]: javadoc/latest/org/robolectric/annotation/ConscryptMode.Mode.html#OFF
[conscrypt-mode-mode-on]: javadoc/latest/org/robolectric/annotation/ConscryptMode.Mode.html#ON
[global-config-provider]: javadoc/latest/org/robolectric/pluginapi/config/GlobalConfigProvider.html
[robolectric-3.1.3-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-3.1.3
[robolectric-3.2-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-3.2
[robolectric-4.9-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-4.9
[robolectric-4.9.1-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-4.9.1
