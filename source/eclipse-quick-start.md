---
title: Eclipse Quick Start
group: Setup
order: 3
---

# Quick Start for Eclipse

_Note:_ If you're using [Maven](http://maven.apache.org/) as your build tool you can include Robolectric tests in
the same project as your application code without any additional configuration using the [Android for Maven Eclipse](http://rgladwell.github.io/m2e-android/) plugin.

## Download JARs

You'll need at least the "robolectric-x.x.x-jar-with-dependencies" jar, which can be obtained [here](/download/).
You may also want the source jar matching your Robolectric version from the same location, in case you run in to any
bugs. Also recommended, but not required, is the [latest hamcrest-all jar](https://code.google.com/p/hamcrest/downloads/list).
 
## Project Creation
Create a project

* File &rarr; New &rarr; Project... &rarr; Android &rarr; Android Project
* Click "Next"

New Android Project dialog

* Project/Application Name: MyProject
* Package Name: com.example.myproject
* Target/Compile With: API 18

Make sure to note the package name, as you'll need it later. Target SDK can't currently be set to anything higher
than API 18, as Robolectric doesn't yet support API 19.

Add a 'test' directory to your project

* Right click on 'MyProject' in the package explorer &rarr; New... &rarr; Folder (**not** Source Folder)
* Folder name: test (do not make this a source folder for this project - hang tight)
* Click "Finish"

Add a 'test' subdirectory under libs

* Right click on 'libs' &rarr; New... &rarr; Folder (Again, **not** a source folder)
* Folder name 'test' 
* Click "Finish"

Finally, drag and drop/otherwise copy your test-related jar files (robolectric/sources/hamcrest) into the libs/test directory.

## Create a *JAVA* project for your tests

Create and configure test Java project (**not** an Android Application Project)

* File &rarr; New &rarr; Java Project...
* Project Name: MyProjectTests
* Under "Project Layout", select "Use project folder as root for sources and class files"
* On Source tab Under "Details", click "Link additional source"
* Browse to and select ".../MyProject/test"
* Click "Finish" on the "Link additional sources" dialog (keep the new Java project dialog open)

Add dependency on the Android project

* Select "Projects" tab at the top of the New Java Project dialog
* Click "Add..."
* Check "MyProject"
* Click "OK"

Add required libraries

* Select "Libraries" tab at the top of the New Java Project dialog
* Robolectric jars
  * Click "Add JARS"
  * Select all jars from within MyProject/libs/test, then click "OK"
* Android jars
  * Click "Add External Jars"
  * Navigate to your SDK install directory, then select "platforms/android-xx/android.jar", choosing the API version to match the one you chose for Target when creating the Android project.
  * Click "Open" to add android.jar
* JUnit
  * Click "Add Library"
  * Select JUnit, then click "Next"
  * Change the JUnit version to 4 (Robolectric requires JUnit 4), then click "Finish"

Set classpath order

  * Select "Order and Export" tab at the top of the New Java Project dialog
  * Select android.jar, and move it down so that it appears below the robolectric jars

Click "Finish", closing the new Java project dialog

Most of these settings can be changed later by right-clicking your test project, selecting "Build path" &rarr; "Configure Build Path"

## Create a test Run Configuration

Your tests will *not* run without this step; Robolectric will not be able to locate your project resources.

* Click "Run" &rarr; "Run Configurations..."
* Double click "*JUnit*" (**not** "Android JUnit Test")
* Name: MyProjectTests
* Select the "Run all tests in the selected project, package or source folder:" radio button
* Click the "Search" button
* Select "MyProjectTest"
* Test runner: JUnit 4
* Click on the link "Multiple launchers available Select one..." at the bottom of the dialog
* Check the "Use configuration specific settings" box
* Select "Eclipse JUnit Launcher"
* Click "OK"
* Click the "Arguments" tab
* Under "Working directory:" select the "Other:" radio button
* Click "Workspace..."
* Select "MyProject" (**not** "MyProjectTest", The value inside of 'Other' edit box should be '${workspace_loc:MyProject}'), then click "OK"
* Click "Apply" then "Close"

## Verify your setup

* Right click the "test" source folder under "MyProjectTest"
* Select "New"&rarr;"Class"
* Package: "com.example"
* Name: "MainActivityTest" (the name of the default activity created in your Android project suffixed with "test"
* Click "Finish"
* Add the following source:

```java
package com.example.myproject;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    MainActivity activity;
    
    @Before
    public void setup()
    {
        this.activity = Robolectric.buildActivity(MainActivity.class).create().get();
    }
    
    @Test
    public void shouldHaveHappySmiles() throws Exception 
    {
        String hello = this.activity.getString(R.string.hello_world);
        assertThat(hello, equalTo("Hello world!"));
    }
}
```

To run the tests

* "Run" &rarr; "Run Configurations..."
* Select "JUnit" &rarr; "MyProjectTests"
* Click "Run"

The tests may take quite a while to start running after the first launch. Robolectric downloads a real Android jar on the first run, placing it in your local maven repo. Future test runs are considerably faster.

## *If you get a RuntimeException saying: "no such layout layout/main"*
It means that you have tried to run a test for which you do not have a Run Configuration set up. To remedy this:

* Right click on the test
* "Run As" &rarr; "Run Configurations..."
* Double click "JUnit" (this will magically make the test you're running appear under JUnit)
* Select "MyActivityTest" (or the name of whichever test you are currently trying to run)
* TestRunner: JUnit 4
* Click on the link "Multiple launchers available Select one..." (or it also may appear as "Using XXX Launcher - Select
other...") at the bottom of the dialog
* Check the "Use configuration specific settings" box
* Select "Eclipse JUnit Launcher"
* Click "OK"
* Click the "Arguments" tab
* Under "Working directory:" select the "Other:" radio button
* Click "Workspace..."
* Select "MyProject" (*not* "MyProjectTest")
* Click "OK"
* Click "Close"
