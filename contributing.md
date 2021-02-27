---
title: Contributor Guidelines
group: Contributing
order: 1
toc: true
---

# Contributor Guidelines

## Getting Started

Dependencies:

* Android SDK with Tools, Extras, and 'Google APIs' for latest API level installed.

Set Android environment variables:

    export ANDROID_HOME=/path-to-sdk-root
    export PATH=${PATH}:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

[Fork](https://github.com/robolectric/robolectric) and clone the repo:

    git clone git@github.com:username/robolectric.git
    
Create a feature branch to make your changes:

    git checkout -b my-feature-name

Robolectric is built using Gradle. Both IntelliJ and Android Studio can import the top-level build.gradle file and will automatically generate their project files from it.

Robolectric supports running tests against multiple Android API levels. The work it must do to support each API level is slightly different, so its shadows are built separately for each. To build shadows for every API version, run:

    ./gradlew clean assemble testClasses --parallel

## Contribution Requirements

### Code Style

Essentially the IntelliJ default Java style, but with two-space indents and Google-style imports.

1. Spaces, not tabs.
2. Two space indent.
3. Curly braces for everything: if, else, etc.
4. One line of white space between methods.
5. No `'m'` or `'s'` prefixes before instance or static variables.
6. Import Google's [java imports style](https://google.github.io/styleguide/javaguide.html#s3.3-import-statements) ([IntelliJ style file here](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)).

If your changes break the code style, the CI will fail, and your CL will be blocked. You can use [google-java-format](https://github.com/google/google-java-format) to format your code locally before you push your changes for reviewing.

### Writing Tests

Robolectric is a unit testing framework and it is important that Robolectric itself be very well tested. All classes should have unit test classes. All public methods should have unit tests. Those classes and methods should have their possible states well tested. Pull requests without tests will be sent back to the submitter.

### Documentation

Robolectric uses [Markdown Doclet](https://github.com/Abnaxos/markdown-doclet), so write javadoc
using [markdown](http://daringfireball.net/projects/markdown/), with javadoc tag extensions.

For developers with the [Robolectric Chrome Extension](https://chrome.google.com/webstore/detail/robolectric/pjepcinimnfnaoopahdkpkefnefdkdgh) installed, javadoc on Robolectric shadow classes and methods are inserted into [developer.android.com](https://developer.android.com/reference/packages.html) sections for the classes and methods they shadow, and identified as Robolectric-related testing notes. Javadoc on visible, non-`@Implementation` methods on shadow classes are displayed in a new 'Testing APIs' section for the shadowed class.

There are special rules for javadoc on shadow classes to support the [Robolectric Chrome Extension](https://chrome.google.com/webstore/detail/robolectric/pjepcinimnfnaoopahdkpkefnefdkdgh):
* All `@Implementation` methods whose behavior varies from the standard Android behavior MUST have Javadoc describing the difference. Use `@see` or `{@link}` to indicate if the method's behavior can be changed or inspected by calling testing API methods. If the method's behavior is identical to the normal framework behavior, no javadoc is necessary.
* All visible non-`@Implementation` methods SHOULD have descriptive Javadoc.
* Don't write javadoc comments like "Shadow for (whatever).". The javadoc will appear in a section clearly related to testing, so make it make sense in context.

### Deprecations and Backwards Compatibility

To provide an easy upgrade path, we aim to always mark methods or classes `@Deprecated` in at least a patch release before removing them in the next minor release. We realize that's not quite how [Semantic Versioning](http://semver.org/) is supposed to work, sorry. Be sure to include migration notes in the `/** @deprecated */` javadoc!
