# Robolectric Simulator

Starting with Robolectric 4.15, it's possible to launch an interactive
preview of your app or tests in a Robolectric environment. This has been made
possible with the significant enhancements to Robolectric over the past four
years, particularly related to native graphics.

Note that the Robolectric simulator is highly experimental and is still largely
in the proof-of-concept stage. If you encounter issues, please file bugs on the
[Robolectric
repository](https://github.com/robolectric/robolectric/issues/new/choose).
Problems you experience when running the simulator are likely problems in
Robolectric itself and can help drive improvements to the testing framework.

The Robolectric simulator can be a useful way to preview how your UI would look
in a Robolectric environment. This can be helpful, for instance, when debugging
failing Robolectric tests. Also note that UI rendered in the Robolectric
simulator may not match an Android emulator or device 100%.

## Launching the simulator

There are two ways to launch the simulator: with the simulator Gradle plugin or
by invoking the simulator APIs from tests.

### Using the Gradle plugin

To enable the Robolectric simulator with the Gradle plugin, add the plugin to
the `plugins` block of your app's `build.gradle`/`build.gradle.kts` file:

!!! snippet "simulator-gradle-plugin"

    /// tab | Groovy
    ```groovy
    id 'org.robolectric.simulator' version '{{ simulator_gradle_plugin.version.current }}'
    ```
    ///

    /// tab | Kotlin
    ```kotlin
    id("org.robolectric.simulator") version "{{ simulator_gradle_plugin.version.current }}"
    ```
    ///

To enable the simulator to work with JDK 23 and newer, you must add the following extra jvmArgs to your unit test configuration:

!!! snippet "simulator-jvmArgs-configurations"

    /// tab | Groovy
    ```groovy
    tasks.withType(Test).configureEach {
        it.jvmArgs(
            "--add-exports=java.desktop/sun.awt=ALL-UNNAMED",
            "--add-exports=java.base/sun.reflect.misc=ALL-UNNAMED",
            "--add-exports=java.base/sun.security.action=ALL-UNNAMED",
            "--add-exports=java.desktop/sun.swing=ALL-UNNAMED",
            "--add-exports=java.desktop/sun.lwawt.macosx=ALL-UNNAMED"
        )
    }
    ```
    ///

    /// tab | Kotlin
    ```kotlin
    testOptions {
        unitTests {
            all { test ->
                test.jvmArgs(
                    "--add-exports=java.desktop/sun.awt=ALL-UNNAMED",
                    "--add-exports=java.base/sun.reflect.misc=ALL-UNNAMED",
                    "--add-exports=java.base/sun.security.action=ALL-UNNAMED",
                    "--add-exports=java.desktop/sun.swing=ALL-UNNAMED",
                    "--add-exports=java.desktop/sun.lwawt.macosx=ALL-UNNAMED",
                )
            }
        }
    }
    ```
    ///

Then, run `./gradlew simulate` to start the simulator. It will run the main
`Activity` that is configured in your manifest.

If the simulator Gradle plugin is not working for you, it is equivalent to
running the example code below in the standalone API.

### Standalone API

Inside a Robolectric test, you can also start the simulator using the
standalone API. To do this, first add a dependency to the simulator in your
project's `build.gradle`/`build.gradle.kts` file.

The following example sets up an `Activity` and subsequently starts the simulator
to preview the `Activity`.

!!! snippet "Simulator dependency"

    /// tab | Groovy
    ```groovy
    testImplementation 'org.robolectric:simulator:{{ robolectric.version.current }}'
    ```
    ///

    /// tab | Kotlin
    ```kotlin
    testImplementation("org.robolectric:simulator:{{ robolectric.version.current }}")
    ```
    ///

Next, you can create a Simulator object and start it using:

!!! snippet "Simulator API"

    /// tab | Java
    ```java
    import static android.os.Build.VERSION_CODES.VANILLA_ICE_CREAM;

    import org.robolectric.annotation.GraphicsMode;
    import org.robolectric.simulator.Simulator;
    import org.junit.Test;
    import org.junit.runner.RunWith;
    import org.robolectric.Robolectric;
    import org.robolectric.RobolectricTestRunner;
    import org.robolectric.annotation.Config;

    @RunWith(RobolectricTestRunner.class)
    @Config(sdk = VANILLA_ICE_CREAM)
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    public class MainActivityTest {
      @Test
      public void simulateMainActivity() throws Exception {
        Robolectric.setupActivity(MainActivity.class);
        new Simulator().start();
      }
    }
    ```
    ///

    /// tab | Kotlin
    ```kotlin
    import android.os.Build.VERSION_CODES.VANILLA_ICE_CREAM
    import org.junit.Test
    import org.junit.runner.RunWith
    import org.robolectric.Robolectric
    import org.robolectric.RobolectricTestRunner
    import org.robolectric.annotation.Config
    import org.robolectric.annotation.GraphicsMode
    import org.robolectric.simulator.Simulator

    @RunWith(RobolectricTestRunner::class)
    @Config(sdk = [VANILLA_ICE_CREAM])
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    class MainActivityTest {
        @Test
        fun simulateMainActivity() {
            Robolectric.setupActivity(MainActivity::class.java)
            Simulator().start()
        }
    }
    ```
    ///

This approach is good if your top-level Activity is unable to be started using
the Gradle plugin.
