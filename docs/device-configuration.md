# Device Configuration

Robolectric makes it easy to simulate a variety of device configurations. In particular, the
properties that make up the [`Configuration`][configuration-documentation] class can be specified at
the test method, test class, package, or suite level, as described [here](configuring.md).

## Specifying device configuration

The Android device configuration can be specified using the [`qualifiers`][config-qualifiers]
[`@Config`][config-documentation] argument:

/// tab | Java

```java
@Test
@Config(qualifiers = "fr-rFR-w360dp-h640dp-xhdpi")
public void testItOnFrenchNexus5() {
}
```

///

/// tab | Kotlin

```kotlin
@Test
@Config(qualifiers = "fr-rFR-w360dp-h640dp-xhdpi")
fun testItOnFrenchNexus5() {
}
```

///

From [version 3.6][robolectric-3.6-release] on, Robolectric parses the `qualifiers` property
according to the rules set forth [here][android-resources-qualifier-rules] (but with no preceding
directory name), and sets up the Android simulation environment with a corresponding configuration.
The system's `Configuration`, [`Display`][display-documentation] and
[`DisplayMetrics`][display-metrics-documentation] objects will all reflect the specified
configuration, the locale will be set, and appropriate resources will be selected.

For unspecified properties, Robolectric picks consistent values based on the properties that have
been specified, or uses default values as follows:

| Property                              | Calculated value (if unspecified)                                                                                                              | Default     | Other rules                                                                         |
|---------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|-------------|-------------------------------------------------------------------------------------|
| MCC and MNC                           | None                                                                                                                                           | None        |                                                                                     |
| Language, region, and script (locale) | None                                                                                                                                           | `en-rUS`    |                                                                                     |
| Layout direction                      | The locale’s layout direction                                                                                                                  | `ldltr`     |                                                                                     |
| Smallest width                        | The smallest of width and height                                                                                                               | `sw320dp`   |                                                                                     |
| Width                                 | If screen size is specified, the corresponding width as declared here                                                                          | `w320dp`    | If screen orientation is specified, width and height will be swapped as appropriate |
| Height                                | If screen size is specified, the corresponding height as declared here. If screen aspect is specified as long, the height is increased by 25%. | `h470dp`    | If screen orientation is specified, width and height will be swapped as appropriate |
| Screen size                           | If height and width are specified, the corresponding screen size as declared here                                                              | `normal`    |                                                                                     |
| Screen aspect                         | If width and height are specified, long will be used if the ratio of height to width is at least 1.75                                          | `notlong`   |                                                                                     |
| Round screen                          | If UI mode is watch then `round`                                                                                                               | `notround`  |                                                                                     |
| Wide color gamut                      | None                                                                                                                                           | `nowidecg`  |                                                                                     |
| High dynamic range                    | None                                                                                                                                           | `lowdr`     |                                                                                     |
| Screen orientation                    | If width and height are specified, `port` or `land` as appropriate                                                                             | `port`      |                                                                                     |
| UI mode                               | None                                                                                                                                           |             | `normal`, except this property isn't included in the qualifier list                 |
| Night mode                            | None                                                                                                                                           | `notnight`  |                                                                                     |
| Screen pixel density                  | None                                                                                                                                           | `mdpi`      |                                                                                     |
| Touchscreen type                      | None                                                                                                                                           | `finger`    |                                                                                     |
| Keyboard availability                 | None                                                                                                                                           | `keyssoft`  |                                                                                     |
| Primary text input method             | None                                                                                                                                           | `nokeys`    |                                                                                     |
| Navigation key availability           | None                                                                                                                                           | `navhidden` |                                                                                     |
| Primary non-touch navigation method   | None                                                                                                                                           | `nonav`     |                                                                                     |
| Platform version                      |                                                                                                                                                |             | The SDK level currently active. Need not be specified.                              |

## Cumulative qualifiers

By default, specifying qualifiers causes any previous specification of qualifiers to be ignored. For
example, qualifiers at the test method level occlude qualifiers at the test class level. However, if
the qualifiers config property starts with a `+` (plus sign), it is interpreted as an overlay to any
higher-level qualifiers that have been specified:

/// tab | Java

```java
@Config(qualifiers = "xlarge-port")
public class MyTest {
  @Test
  public void testItWithXlargePort() {
    // Config is "xlarge-port"
  }

  @Test
  @Config(qualifiers = "+land")
  public void testItWithXlargeLand() {
    // Config is "xlarge-land"
  }

  @Test
  @Config(qualifiers = "land")
  public void testItWithLand() {
    // Config is "normal-land"
  }
}
```

///

/// tab | Kotlin

```kotlin
@Config(qualifiers = "xlarge-port")
class MyTest {
  @Test
  fun testItWithXlargePort() {
    // Config is "xlarge-port"
  }

  @Test
  @Config(qualifiers = "+land")
  fun testItWithXlargeLand() {
    // Config is "xlarge-land"
  }

  @Test
  @Config(qualifiers = "land")
  fun testItWithLand() {
    // Config is "normal-land"
  }
}
```

///

Values for unspecified properties are calculated, and rules are applied, after all configs have been
merged.

## Changing device configuration

The device configuration can be changed within a test using
[`RuntimeEnvironment.setQualifiers()`][runtime-environment-set-qualifiers]:

/// tab | Java

```java
@Test
@Config(qualifiers = "+port")
public void testOrientationChange() {
  MyActivity controller = Robolectric.buildActivity(MyActivity.class);
  controller.setup();
  // assert that activity is in portrait mode
  RuntimeEnvironment.setQualifiers("+land");
  controller.configurationChange();
  // assert that activity is in landscape mode
}
```

///

/// tab | Kotlin

```kotlin
@Test
@Config(qualifiers = "+port")
fun testOrientationChange() {
  val controller = Robolectric.buildActivity(MyActivity::class)
  controller.setup()
  // assert that activity is in portrait mode
  RuntimeEnvironment.setQualifiers("+land")
  controller.configurationChange()
  // assert that activity is in landscape mode
}
```

///

The string parameter to `RuntimeEnvironment.setQualifiers()` has the same rules as
`Config.qualifiers`.

Note that `RuntimeEnvironment.setQualifiers()` updates the system and application resources with the
new configuration, but does not trigger any action on extant activities or other components.
[`ActivityController.configurationChange()`][activity-controller-configuration-change] can be used
to simulate the sequence of events that take place on a device when its configuration changes.

If the [`Activity`][activity-documentation] is configured to handle the configuration changes,
`ActivityController.configurationChange()` will call the `Activity`’s `onConfigurationChanged()`
method. If not, `ActivityController` destroys and recreates the `Activity`.

## Simulating displays

Robolectric allows display properties to be changed during a test using setters on
[`ShadowDisplay`][shadow-display]. Multiple displays can be simulated using APIs on
[`ShadowDisplayManager`][shadow-display-manager].

[activity-controller-configuration-change]: javadoc/latest/org/robolectric/android/controller/ActivityController.html#configurationChange(android.content.res.Configuration)
[activity-documentation]: https://developer.android.com/reference/android/app/Activity
[android-resources-qualifier-rules]: https://developer.android.com/guide/topics/resources/providing-resources.html#QualifierRules
[config-documentation]: javadoc/latest/org/robolectric/annotation/Config.html
[config-qualifiers]: javadoc/latest/org/robolectric/annotation/Config.html#qualifiers()
[configuration-documentation]: https://developer.android.com/reference/android/content/res/Configuration
[display-documentation]: https://developer.android.com/reference/android/view/Display
[display-metrics-documentation]: https://developer.android.com/reference/android/util/DisplayMetrics
[robolectric-3.6-release]: https://github.com/robolectric/robolectric/releases/tag/robolectric-3.6
[runtime-environment-set-qualifiers]: javadoc/latest/org/robolectric/RuntimeEnvironment.html#setQualifiers(java.lang.String)
[shadow-display]: javadoc/latest/org/robolectric/shadows/ShadowDisplay.html
[shadow-display-manager]: javadoc/latest/org/robolectric/shadows/ShadowDisplayManager.html
