---
hide:
- toc
---

<!-- markdownlint-disable MD029 -->

# Automated Migration

Robolectric provides an automated migration tool to help keep your test suite up to date with
Robolectric API changes. It's based on [Error Prone][error-prone-refactoring]'s refactoring tools.

The migration tool will make changes directly to source files in your codebase, which you can review
and commit to your source control system.

**Before** updating your dependencies to the new version of Robolectric:

1. Make sure you're using a recent version of Gradle (4.10 or newer).

2. [Configure your project][error-prone-setup] to integrate Error Prone. Quick config for Gradle (
   usually in your module's `build.gradle`/`build.gradle.kts` file):

/// tab | Groovy

```groovy
plugins {
   id "net.ltgt.errorprone" version "<error_prone_plugin_version>" apply false
}

String robolectricMigrations = System.getenv("ROBOLECTRIC_MIGRATIONS")
if (robolectricMigrations) {
   apply plugin: "net.ltgt.errorprone"

   dependencies {
      errorprone "com.google.errorprone:error_prone_core:<error_prone_version>"
      errorprone "org.robolectric:errorprone:{{ robolectric.version.current }}"
   }

   tasks.withType(JavaCompile).configureEach {
      options.errorprone.errorproneArgs = [
              '-XepPatchChecks:' + robolectricMigrations,
              '-XepPatchLocation:IN_PLACE',
      ]
   }
}
```

///

/// tab | Kotlin

```kotlin
plugins {
   id("net.ltgt.errorprone") version "<error_prone_plugin_version>" apply false
}

val robolectricMigrations = System.getenv("ROBOLECTRIC_MIGRATIONS")
if (!robolectricMigrations.isNullOrEmpty()) {
   pluginManager.apply("net.ltgt.errorprone")

   dependencies {
      errorprone("com.google.errorprone:error_prone_core:<error_prone_version>")
      errorprone("org.robolectric:errorprone:{{ robolectric.version.current }}")
   }

   tasks.withType<JavaCompile>().configureEach {
      options.errorprone.errorproneArgs = listOf(
         "-XepPatchChecks:$robolectricMigrations",
         "-XepPatchLocation:IN_PLACE",
      )
   }
}
```

///

    You don't need to commit this change.

3. Run the migrations:

    ```bash
    ROBOLECTRIC_MIGRATION=DeprecatedMethods,ShadowUsageCheck ./gradlew clean :compileDebugUnitTestJava
    ```

4. Make sure your code still compiles and commit changes.
5. Update your project to the new version of Robolectric.

The migration tool will make a best effort attempt to adjust the source code, but there might be
more complicated situations that it cannot handle and that need to be converted manually.

[error-prone-refactoring]: https://errorprone.info/docs/patching
[error-prone-setup]: https://errorprone.info/docs/installation
