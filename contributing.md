---
title: Contributor Guidelines
group: Contributing
order: 1
toc: true
---

# Contributor Guidelines

## Getting Started

[Fork](https://github.com/robolectric/robolectric) and clone the repo:

    git clone git@github.com:username/robolectric.git
    
Install all required dependencies:

    ./scripts/install-dependencies.sh

Create a feature branch to make your changes:

    git co -b my-feature-name
    
Perform a full build of all shadows:

    ./gradlew install

We develop Robolectric on Mac and Linux. You might be able to figure out how to get it to work on Windows if you really want to for some reason. Good luck.

## Contribution Requirements

### Code Style

Essentially the IntelliJ default Java style, but with two-space indents and Google-style imports.

1. Spaces, not tabs.
2. Two space indent.
3. Curly braces for everything: if, else, etc.
4. One line of white space between methods.
5. No `'m'` or `'s'` prefixes before instance or static variables.
6. Import Google's [java imports style](https://google.github.io/styleguide/javaguide.html#s3.3-import-statements) ([IntelliJ style file here](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)).

### Writing Tests

Robolectric is a unit testing framework and it is important that Robolectric itself be very well tested. All classes should have unit test classes. All public methods should have unit tests. Those classes and methods should have their possible states well tested. Pull requests without tests will be sent back to the submitter.

### Documentation

Robolectric uses [Markdown Doclet](https://github.com/Abnaxos/markdown-doclet), so write javadoc
using [markdown](http://daringfireball.net/projects/markdown/), with javadoc tag extensions.

On [developer.android.com](https://developer.android.com/reference/packages.html), class-level and method-level javadoc on shadow classes is inserted into the javadoc for the class it shadows, and identified as Robolectric-related testing notes. Javadoc on visible, non-`@Implementation` methods on shadow classes are displayed in a new 'Testing APIs' section of the page.

There are special rules for javadoc on shadow classes to support the [Robolectric Chrome Extension](https://chrome.google.com/webstore/detail/robolectric/pjepcinimnfnaoopahdkpkefnefdkdgh):
* All `@Implementation` methods whose behavior varies from the standard Android behavior MUST have Javadoc describing the difference. Use `@see` or `{@link}` to indicate if the method's behavior can be changed or inspected by calling testing API methods. If the method's behavior is identical to the normal framework behavior, no javadoc is necessary.
* All visible non-`@Implementation` methods SHOULD have descriptive Javadoc.
* Don't write javadoc comments like "Shadow for (whatever).". The javadoc will appear in a section clearly related to testing, so make it make sense in context.

### Deprecations and Backwards Compatibility

To provide an easy upgrade path, we aim to always mark methods or classes `@Deprecated` in at least a patch release before removing them in the next minor release. We realize that's not quite how [Semantic Versioning](http://semver.org/) is supposed to work, sorry. Be sure to include migration notes in the `/** @deprecated */` javadoc!
