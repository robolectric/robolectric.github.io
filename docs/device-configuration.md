---
hide:
- footer
---

# Device Configuration

Robolectric makes it easy to simulate a variety of device configurations. In particular, the properties that make up the `android.content.res.Configuration` class can be specified at the test method, test class, package, or suite level, as described [here](configuring.md).

## Specifying Device Configuration
The Android device configuration can be specified using the `qualifiers` config property:

```java
@Test @Config(qualifiers = "fr-rFR-w360dp-h640dp-xhdpi")
public void testItOnFrenchNexus5() { ... }
```

From version 3.6 on, Robolectric parses the `qualifiers` property according to the rules set forth [here](https://developer.android.com/guide/topics/resources/providing-resources.html#QualifierRules) (but with no preceding directory name), and sets up the Android simulation environment with a corresponding configuration. The system's `Configuration`, `Display` and `DisplayMetrics` objects will all reflect the specified configuration, the locale will be set, and appropriate resources will be selected.

For unspecified properties, Robolectric picks consistent values based on the properties that have been specified, or uses default values as follows:

| Property         | Calculated value (if unspecified) | Default | Other rules |
|-----|-----|-----|-----|
| MCC and MNC      | None. | None | |
| Language, region, and script (locale) | None. | `en-rUS` | |
| Layout direction | The locale’s layout direction. | `ldltr` | |
| Smallest width   | The smaller of width and height | `sw320dp` | |
| Width            | If screen size is specified, the corresponding width as declared here. | `w320dp` | If screen orientation is specified, width and height will be swapped as appropriate. |
| Height           | If screen size is specified, the corresponding height as declared here. If screen aspect is specified as long, the height is increased by 25%. | `h470dp` | If screen orientation is specified, width and height will be swapped as appropriate. |
| Screen size      | If height and width are specified, the corresponding screen size as declared here. | `normal` | |
| Screen aspect    | If width and height are specified, long will be used if the ratio of height to width is at least 1.75. | `notlong` | |
| Round screen     | If UI mode is watch then round. | `notround` | |
| Wide color gamut | None. | `nowidecg` | |
| High dynamic range | None. | `lowdr` | |
| Screen orientation | If width and height are specified, port or land as appropriate. | `port` | |
| UI mode          | None. | | `normal`, except this property isn't included in the qualifier list. |
| Night mode       | None. | `notnight` | |
| Screen pixel density | None. | `mdpi` | |
| Touchscreen type | None. | `finger` | |
| Keyboard availability | None. | `keyssoft` | |
| Primary text input method | None. | `nokeys` | |
| Navigation key availability | None. | `navhidden` | |
| Primary non-touch navigation method | None. | `nonav` | |
| Platform version | | | The SDK level currently active. Need not be specified. |

## Cumulative Qualifiers

By default, specifying qualifiers causes any previous specification of qualifiers to be ignored. For example, qualifiers at the test method level occlude qualifiers at the test class level. However, if the qualifiers config property starts with a ‘+’ (plus sign), it is interpreted as an overlay to any higher-level qualifiers that have been specified:

```java
@Config(qualifiers = "xlarge-port")
class MyTest {
  public void testItWithXlargePort() { ... } // config is "xlarge-port"

  @Config(qualifiers = "+land")
  public void testItWithXlargeLand() { ... } // config is "xlarge-land"

  @Config(qualifiers = "land")
  public void testItWithLand() { ... } // config is "normal-land"
}
```

Values for unspecified properties are calculated, and rules are applied, after all configs have been merged.

## Changing Device Configuration
The device configuration can be changed within a test using `RuntimeEnvironment.setQualifiers()`:

```java
@Test @Config(qualifiers = "+port")
public void testOrientationChange() {
  controller = Robolectric.buildActivity(MyActivity.class);
  controller.setup();
  // assert that activity is in portrait mode
  RuntimeEnvironment.setQualifiers("+land");
  controller.configurationChange();
  // assert that activity is in landscape mode
}
```

The string parameter to `setQualifiers()` has the same rules as `Config.qualifiers`.

Note that `RuntimeEnvironment.setQualifiers()` updates the system and application resources with the new configuration, but does not trigger any action on extant activities or other components. [`ActivityController.configurationChange()`](javadoc/latest/org/robolectric/android/controller/ActivityController.html#configurationChange-android.content.res.Configuration-) can be used to simulate the sequence of events that take place on a device when its configuration changes.

If the activity is configured to handle the configuration changes, `ActivityController.configurationChange()` will call the activity’s `onConfigurationChanged()` method. If not, `ActivityController` destroys and recreates the activity.

## Simulating Displays

Robolectric allows display properties to be changed during a test using setters on `ShadowDisplay`. For Jelly Bean MR1 and up, multiple displays can be simulated using APIs on `ShadowDisplayManager`. See their documentation for more details.
