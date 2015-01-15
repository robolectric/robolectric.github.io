---
title: Contributor Guidelines
group: Contributing
order: 1
---

# Coding Guidelines

## Functionality should have appropriate unit tests

Robolectric is a unit testing framework and it is important that Robolectric itself be very well tested. All classes
should have unit test classes. All public methods should have unit tests. Those classes and methods should have their
possible states well tested. Copied Android source should at least have "smoke tests" that assure the copied
functionality is wired up correctly. Pull requests introducing untested functionality should not accepted, and
reviewers should give appropriate feedback to the submitter.

## Code can be copied from Android source when appropriate

"When appropriate" is subjective. In an effort to avoid complexity, copying Android source as the basis for Shadow
object functionality is discouraged. That said, sometimes the functionality is complex and the Android implementation
is what is needed. Contributors and reviewers should use their best judgement: should a 3000-line Android class be
copied to gain access to a boolean getter and setter? It depends.

## Follow the code formatting standard

This is essentially the IntelliJ default Java style, but with two-space indents.

* Spaces, not tabs.
* Indenting: http://en.wikipedia.org/wiki/Indent_style#Variant:_1TBS but with two spaces, not four.
* Curly braces for everything: if, else, etc.
* One line of white space between methods

# Building and Testing

You can build Robolectric on the command-line, or from within IntelliJ.

## Command-line

In the command-line, there are a few scripts that you need to run to install correctly. If you are ever unsure, then check the [Travis build file](https://github.com/robolectric/robolectric/blob/master/.travis.yml), which should contain up-to-date build instructions.

The following instructions assume that you have an `ANDROID_HOME` environment variable pointing to the location of the Android SDK, and that the `android` executable is available from the `PATH`. If this is not the case, then run:

    export ANDROID_HOME=/path/to/my/android/sdk
    export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools

To install dependencies, you will need to run:

    echo "y" | android update sdk --no-ui --filter platform-tools,tools
    echo "y" | android update sdk --no-ui --filter build-tools-20.0.0
    echo "y" | android update sdk --no-ui --filter android-18
    echo "y" | android update sdk --no-ui --filter addon-google_apis-google-18,extra-android-support
    ./scripts/install-maps-jar.sh
    ./scripts/install-support-jar.sh
    
Then to build Robolectric and run the unit tests, you will need to do:

    ./scripts/install-robolectric.sh

Currently these build instructions do not support Windows; only *nix environments.

## IntelliJ

In IntelliJ, choose "Import Project," then choose the `pom.xml` in the root directory. You can continue clicking "Next" at this point. You do not need to change any of the default options, i.e. you don't need to tick "Import maven projects automatically" or "Search for projects recursively."

If it fails to build, you may need to run `mvn compile` in the command line at the root directory.

## Running individual tests

Since the tests take awhile, you may be inclined to run them individually. To do this in IntelliJ, select the test you want to run in the left-hand panel, then choose **Run**, then **Edit Configurations**, then add `robolectric/` to the end of the **Working Directory**. Finally, choose **Run NameOfTheTest**.

If you don't modify the run configuration, then you will see errors, because the tests expect to be run from that directory.