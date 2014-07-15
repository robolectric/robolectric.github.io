---
title: Configuring Robolectric
group: User Guide
order: 2
---

# Configuring Robolectric

There are numerous ways to configure certain behaviors of Robolectric at runtime.

## System Properties

You can configure some global behaviors of Robolectric by setting these system properties:

* **robolectric.strictI18n** - set to "true" to enable i18n-strict mode
* **robolectric.logging** - may be "stdout", "stderr", or a filename
* **robolectric.offline (new in 2.4)** - set to "true" to disable runtime fetching of jars from Maven
* **robolectric.dependency.dir (new in 2.4)** - when in offline mode, specifies a folder containing runtime dependencies such as the Android jar

## Robolectric Config File

You can set default values for any item in the [Config](/javadoc/org/robolectric/annotation/Config.html) object by including a special properties file on your classpath. The file must be named `org.robolectric.Config.properties` and should contain config values such as:

```
manifest=../myapp/AndroidManifest.xml
shadows=my.package.ShadowFoo
```

## project.properties File

If your project uses any ApkLibs, you will have to set these up to work correctly with Robolectric. This
simply requires creating a `project.properties` file for your project that declares the dependencies to
Robolectric.

The `project.properties` file for your project should sit in the root of the project and looks something like this:

```
target=android-<android API level to target>
android.library.reference.1=<relative path to first dependency>
android.library.reference.2=<relative path to second dependency>
```

Robolectric will look for and then parse this file to load dependencies before any tests run.

### Note: multiple dependency locations

If you're working in an IDE such as IntelliJ and then pushing to a CI that uses Maven, you might run into
the problem of a dependency appearing in different places depending on the environment - the IDE will probably compile to a different directory structure than Maven would. To deal with this, Robolectric will only attempt to load dependencies that exist at run time. This means you can just list both possible locations for the dependency like so:

```
android.library.reference.1=dev-env-dir/my-awesome-dependency
android.library.reference.2=ci-env-dir/my-awesome-dependency
```

In addition, you have the option to create a separate file called `test-project.properties`. If this file is present, Robolectric will use the values there instead.
