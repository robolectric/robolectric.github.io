# Contributor Guidelines

## Getting Started

The first step to contributing is to ensure that you are able to build Robolectric. Please visit [building Robolectric](building-robolectric.md) for more details and instructions on setting up an environment to build Robolectric.

Once you are able to build Robolectric, create a feature branch to make your changes:

    git checkout -b my-feature-name

Robolectric is built using Gradle. It is recommended to use IntelliJ to import the top-level build.gradle file, which will automatically generate their project files from it. It is also possible to use newer versions of Android Studio to build Robolectric as well. 

Follow [Robolectric's README.md](https://github.com/robolectric/robolectric/blob/master/README.md#building-and-contributing) to install other build-tools outside of normal Android toolchain and run tests locally to test your changes before sending PR.

## Contribution Requirements

### Code Style

#### Java code style

Essentially the IntelliJ default Java style, but with two-space indents and Google-style imports.

1. Spaces, not tabs.
2. Two space indent.
3. Curly braces for everything: if, else, etc.
4. One line of white space between methods.
5. No `'m'` or `'s'` prefixes before instance or static variables.
6. Import Google's [java imports style](https://google.github.io/styleguide/javaguide.html#s3.3-import-statements) ([IntelliJ style file here](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)).

If your changes break the code style, the CI will fail, and your CL will be blocked. You can use [google-java-format](https://github.com/google/google-java-format) to format your code locally before you push your changes for reviewing. The [wiki's Running google java format section](https://github.com/robolectric/robolectric/wiki/Running-google-java-format) is a tutorial for it.

#### Kotlin code style

Robolectric uses [Spotless](https://github.com/diffplug/spotless) + [ktfmt](https://github.com/facebookincubator/ktfmt) to apply Google's code style for Kotlin. Please follow [wiki's Robolectric's code style section](https://github.com/robolectric/robolectric/wiki/Robolectric's-code-style) to apply Kotlin format for Kotlin modules and code.

### Writing Tests

Robolectric is a unit testing framework and it is important that Robolectric itself be very well tested. All classes should have unit test classes. All public methods should have unit tests. Those classes and methods should have their possible states well tested. Pull requests without tests will be sent back to the submitter.

If change is related to third-party tool, e.g. Mockito and Mockk, please consider to create related module or tests at [Robolectric's integration_tests](https://github.com/robolectric/robolectric/tree/master/integration_tests) to test third-party too's regression.

If tests need to check that an exception is thrown, use JUnit's `assertThrows` instead of `@Test(expected = SomeException.class)`. Using assertThrows allows more precision to check exactly which line throws a particular exception.

### Documentation

Robolectric uses javadoc to document API's behavior. There are special rules for javadoc on shadow classes:

* All `@Implementation` methods whose behavior varies from the standard Android behavior MUST have Javadoc describing the difference. Use `@see` or `{@link}` to indicate if the method's behavior can be changed or inspected by calling testing API methods. If the method's behavior is identical to the normal framework behavior, no javadoc is necessary.
* All visible non-`@Implementation` methods SHOULD have descriptive Javadoc.
* Don't write javadoc comments like "Shadow for (whatever).". The javadoc will appear in a section clearly related to testing, so make it make sense in context.

Robolectric will release javadoc at {{ config.site_url }} after every main version released. For example, Robolectric's {{ robolectric.version.current }} javadoc is released at {{ config.site_url }}javadoc/{{ robolectric.version.current }}/.

### Deprecations and Backwards Compatibility

To provide an easy upgrade path, we aim to always mark methods or classes `@Deprecated` in at least a patch release before removing them in the next minor release. We realize that's not quite how [Semantic Versioning](http://semver.org/) is supposed to work, sorry. Be sure to include migration notes in the `/** @deprecated */` javadoc!

## Discussion

Robolectric welcome discussion in the entire contribution cycle. If you have any idea or question, you can post on [GitHub Discussion](https://github.com/robolectric/robolectric/discussions) or [Google Groups](https://groups.google.com/g/robolectric). The [GitHub Discussion](https://github.com/robolectric/robolectric/discussions) is the first choice for discussion if you have GitHub account, because it can help to accumulate community knowledge along with existing GitHub issues.   
