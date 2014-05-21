---
title: Quick Start
group: Setup
order: 2
---

# Quick Start

The quickest way to get started using Robolectric is to add the pre-built jar with all dependencies included to your
current project. Instructions for other setup methods are covered below.

Use the `@RunWith` annotation available in JUnit 4 to run your tests under Robolectric:

```java
@RunWith(RobolectricTestRunner.class)
public class MyActivityTest {
    @Test
    public void shouldHaveApplicationName() throws Exception {
        String appName = new MyActivity().getResources().getString(R.string.app_name);
        assertThat(appName, equalTo("MyActivity"));
    }
}
```

If you'd like to quickly get started using Robolectric to test your app, these instructions are for you. They will show
you how to get started with Robolectric using a pre-built jar. While this is a good place to get started, you may find
later that you want to make larger changes and contribute to Robolectric. A later section will explain how to start
using Robolectric as a sub-module of your project.

Probably the easiest way to get started is to create a Maven project and then import it into your IDE of choice.
However we have provided IDE specific instructions if that is the method you prefer.

## Using Maven

This is the method we recommend.

If you are using Maven and the excellent [maven-android-plugin](http://code.google.com/p/maven-android-plugin/) to
build your project, simply add this to the dependencies section of your `pom.xml`:

```xml
<dependency>
    <groupId>com.pivotallabs</groupId>
    <artifactId>robolectric</artifactId>
    <version>X.X.X</version>
    <scope>test</scope>
</dependency>
```

## Using IntelliJ

It just works!

## Using Eclipse

Eclipse will try to use the Android JUnit test runner by default for Android projects, so you will need a separate test
project to run the test with Robolectric, and set up a run configuration to run the tests with the Eclipse JUnit
Launcher.

* Create a new Java project in parallel with your app's project, as your test project. For example, if your app is named
MyApp, you might create a MyAppRobolectricTest project.

* In many cases it will be advantageous to keep the source code for the tests under the same root folder as the source
for the rest of the project. To make this work, use the "Link Source..." button in the Build Path dialog to create a
link from this test project to the source root for the tests under the main project.

* Add the appropriate Android SDK jars to the test project's build path (e.g.
`{android sdk root}/platforms/android-8/android.jar` and
`{android sdk root}/add-ons/addon_google_apis_google_inc_8/libs/maps.jar`)

* [Download](http://pivotal.github.com/robolectric/download.html) robolectric-X.X.X-jar-with-dependencies.jar place it
in your test project and add it to the build path, along with the JUnit library.

* Add your app's project as a project dependency to the build path of your test project.

* _Important!_ Add a new JUnit run/debug launch configuration to run the tests in the test folder. There may be a
warning that multiple launchers are available, make sure to select the Eclipse JUnit Launcher instead of the Android
JUnit Launcher. <b>Set the working directory to be the root of your main project, not that of your test project.</b>

## In any configuration

* Use the `@RunWith` annotation available in JUnit 4 to run your tests under Robolectric:

```java
@RunWith(RobolectricTestRunner.class)
public class MyTest {
    ...
}
```

## Setting up for Robolectric development

If you find that you need to extend or modify Robolectric's simulation of Android or you'd like to contribute to the
project, these instructions will help get you started. You can also track the progress of Robolectric as it evolves from
its [Tracker page](http://www.pivotaltracker.com/projects/105008)

## Using Maven

Clone the robolectric git repository, modify it as needed, and use maven to install a snapshot of Robolectric:

* `git clone git://github.com/robolectric/robolectric.git`

* Modify `pom.xml` and modify Robolectric's version by appending `-SNAPSHOT` if it's not there already (e.g. `0.9.6-SNAPSHOT`)

* `mvn install`

Modify your project's pom to depend on the the same version of Robolectric as you specified in the Robolectric pom.

To edit your project and Robolectric side-by-side in your IDE, follow the instructions above, and open the Robolectric
pom as a module (in IntelliJ) or a separate project (in Eclipse).


## Using IntelliJ without Maven

* [Download](http://pivotal.github.com/robolectric/download.html) robolectric-X.X.X-jar-with-dependencies.jar and add it to your other test
library dependencies (such as junit.jar).

## Using Eclipse without Maven

* Follow the instructions for Eclipse quick start above, but don't download the robolectric-X.X.X-jar-with-dependencies.jar.
Instead, use git to clone the Robolectric repository:

  `git clone git://github.com/robolectric/robolectric.git`

(You'll probably want to fork the repo on github first so you can submit pull requests back to us.)

* Add robolectric as an Eclipse project and make your test project depend on it.

* Add the Android SDK jars to the robolectric project, as above.
