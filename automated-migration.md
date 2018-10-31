---
title: Automated Migration
group: [hide]
order: 7
toc: true
---

# Automated Migration

Robolectric provides an automated migration tool to help keep your test suite up to date with Robolectric API changes. It's based on [Error Prone](https://errorprone.info/docs/patching)'s refactoring tools.

The migration tool will make changes directly to source files in your codebase, which you can review and commit to your source control system.

***Before*** updating your dependencies to the new version of Robolectric:

1. Make sure you're using a recent version of Gradle (4.10 or newer).

2. [Configure your project](https://errorprone.info/docs/installation) to compile using Error Prone. Quick config for Gradle (usually in `app/build.gradle`):

    ```groovy
    plugins {
        id "net.ltgt.errorprone" version "0.6" apply false
    }

    String roboMigration = System.getenv("ROBOLECTRIC_MIGRATION")
    if (roboMigration) {
        apply plugin: "net.ltgt.errorprone"

        dependencies {
            errorprone "com.google.errorprone:error_prone_core:2.3.2"
            errorproneJavac "com.google.errorprone:javac:9+181-r4173-1"

            errorprone "org.robolectric:errorprone:{{ site.robolectric.version.preview | escape }}"
        }

        afterEvaluate {
            tasks.withType(JavaCompile) { t ->
                options.errorprone.errorproneArgs += [
                        '-XepPatchChecks:' + roboMigration,
                        '-XepPatchLocation:IN_PLACE',
                ]
            }
        }
    }
    ```

    You don't need to commit this change.

3. Run the migrations. Due to limitations of Error Prone, you'll need to manually do this in a couple steps:

    ```bash
    ROBOLECTRIC_MIGRATION=DeprecatedMethods ./gradlew clean :compileDebugUnitTestJava
    ROBOLECTRIC_MIGRATION=ShadowUsageCheck ./gradlew clean :compileDebugUnitTestJava
    ```

4. Make sure your code still compiles and commit changes.

5. Update your project to the new version of Robolectric.

The migration tool will make a best effort attempt to adjust source, but there might be more complicated situations that it cannot handle and that need to be converted manually.