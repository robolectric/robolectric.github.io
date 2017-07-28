---
title: Contributor Guidelines
group: Contributing
order: 1
toc: true
---

# Contributor Guidelines

## Getting Started

Fork and clone the repo:

    git clone git@github.com:username/robolectric.git
    
Create a feature branch to make your changes:

    git co -b my-feature-name
    
Install all required dependencies:

    ./scripts/install-dependencies.sh

Perform a full build of all shadows:

    ./gradlew install
    
## Contribution Requirements

### Writing Tests

Robolectric is a unit testing framework and it is important that Robolectric itself be very well tested. All classes should have unit test classes. All public methods should have unit tests. Those classes and methods should have their possible states well tested. Pull requests without tests will be sent back to the submitter.

### Code Style

Essentially the IntelliJ default Java style, but with two-space indents.

1. Spaces, not tabs.
2. Two space indent.
3. Curly braces for everything: if, else, etc.
4. One line of white space between methods.
5. No `'m'` or `'s'` prefixes before instance or static variables.

### Documentation

Robolectric uses [Markdown Doclet](https://github.com/Abnaxos/markdown-doclet), so write javadoc
using [markdown](http://daringfireball.net/projects/markdown/), with javadoc tag extensions.

There are special rules for javadoc on shadow classes to support the [Robolectric Chrome Extension](https://chrome.google.com/webstore/detail/robolectric/pjepcinimnfnaoopahdkpkefnefdkdgh).

On [developer.android.com](https://developer.android.com/reference/packages.html), class-level and method-level javadoc on shadow classes is inserted into the javadoc for the class it shadows, and identified as Robolectric-related testing notes. Javadoc on visible, non-`@Implementation` methods on shadow classes are displayed in a new 'Testing APIs' section of the page.

All `@Implementation` methods whose behavior varies from the standard Android behavior MUST have Javadoc describing the difference. Use `@see` or `{@link}` to indicate if the method's behavior can be changed or inspected by calling testing API methods.

All non-`@Implementation` methods SHOULD have descriptive Javadoc.
