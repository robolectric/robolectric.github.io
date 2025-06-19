# Robolectric Simulator

Starting with the upcoming Robolectric 4.15 release, it's possible to launch an
interactive preview of your app or tests in a Robolectric environment. This has
been made possible with the significant enhancements to Robolectric over the
past four years, particularly related to native graphics.

Note that the Robolectric simulator is highly experimental and is still largely
in the proof-of-concept stage. If you encounter issues, please file bugs on the
[Robolectric
repository](https://github.com/robolectric/robolectric/issues/new/choose).
Problems you experience when running the simulator are likely problems in
Robolectric itself and can help drive improvements to the testing framework.

The Robolectric simulator can be a useful way to preview how your UI would look
in a Robolectric environment. This can be helpful, for instance, when debugging
failing Robolectric tests.

## Launching the simulator

There are two ways to launch the simulator: with the simulator Gradle plugin or
by invoking the simulator APIs from tests.

### Using the Gradle plugin

To enable the Robolectric simulator with the Gradle plugin, add the plugin
to the `plugins` block of your app's `build.gradle`/`build.gradle.kts` file:

!!! snippet "simulator-gradle-plugin"

    /// tab | Groovy
    ```groovy
    id 'org.robolectric.simulator' version '1.0'
    ```
    ///

    /// tab | Kotlin
    ```kotlin
    id("org.robolectric.simulator") version "1.0"
    ```
    ///

Then, run `./gradlew simulate` to start the simulator. It will run the main
`Activity` that is configured in your manifest.

### Standalone API

Inside a Robolectric test, you can also start the simulator using the
standalone API. To do this, first add a dependency to the simulator in your
project's `build.gradle`/`build.gradle.kts` file.

!!! snippet "Simulator dependency"

    /// tab | Groovy
    ```groovy
    testImplementation 'org.robolectric:simulator:4.15-beta-1'
    ```
    ///

    /// tab | Kotlin
    ```kotlin
    testImplementation("org.robolectric:simulator:4.15-beta-1")
    ```
    ///

Next, you can create a Simulator object and start it using:

!!! snippet "Simulator API"

    /// tab | Java
    ```java
    import org.robolectric.simulator.Simulator;
    new Simulator().start();
    ```
    ///

    /// tab | Kotlin
    ```kotlin
    import org.robolectric.simulator.Simulator

    Simulator().start()
    ```
    ///

This approach is good if your top-level Activity is unable to be started in a
Robolectric environment. 
