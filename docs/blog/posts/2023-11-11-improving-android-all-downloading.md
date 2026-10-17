---
date:
  created: 2023-11-11
  updated: 2026-10-17
authors:
  - utzcoz
slug: improving-android-all-downloading
---

# Improving android-all Downloading on CI

> [!NOTE]
> **Updated on October 17, 2026:** An earlier version of this post described
> `robolectric.dependency.dir` as part of an "offline mode" enabled by `robolectric.offline`. In
> fact, Robolectric uses `robolectric.dependency.dir` whenever it is set, regardless of the value of
> `robolectric.offline`. The section about loading android-all jars from a local directory has been
> corrected. See [robolectric#11402][robolectric-issue-11402] for more details.

In recent years, the team has received multiple issues regarding Robolectric's inability to
download/resolve the necessary android-all jars when running Robolectric tests in a CI environment.
Some examples include:

1. [android-all not downloaded as part of robolectric, or is it a separate dependency?][robolectric-issue-7886]
2. [Robolectric failing because not downloading dependencies in Jenkins when using Artifactory][robolectric-issue-8158]
3. [Flaky SHA mismatch on CI builds when retrieving Maven artifacts since upgrading to 4.10.x][robolectric-issue-8205]

Robolectric downloads the necessary android-all jars using its
[MavenArtifactFetcher][maven-artifact-fetcher] when running Robolectric tests. It does not use any
proxies defined by the Gradle build system. In a CI environment, especially in environments used by
large companies internally, there are often network restrictions that can cause the aforementioned
issues. This article provides some solutions to mitigate these issues as much as possible,
including setting a custom proxy for `MavenArtifactFetcher`, loading android-all jars from a local
directory, and manually fetching the necessary android-all jars before running Robolectric tests.

## Setting custom proxy for `MavenArtifactFetcher`

The first solution is setting a custom proxy for `MavenArtifactFetcher` like the following snippet:

```kotlin
testOptions {
    unitTests {
        includeAndroidResources = true
        returnDefaultValues = true
        isIncludeUnitTestDependencies = true
        all {
            it.systemProperty("robolectric.dependency.repo.url", "https://local-mirror/repo")
            it.systemProperty("robolectric.dependency.repo.id", "local")
            // Username and password only needed when local repository
            // needs account information.
            it.systemProperty("robolectric.dependency.repo.username", "username")
            it.systemProperty("robolectric.dependency.repo.password", "password")
            // Since Robolectric 4.9.1, these are available
            it.systemProperty("robolectric.dependency.proxy.host", System.getenv("ROBOLECTRIC_PROXY_HOST"))
            it.systemProperty("robolectric.dependency.proxy.port", System.getenv("ROBOLECTRIC_PROXY_PORT"))
        }
    }
}
```

The `MavenArtifactFetcher` supports the above system properties to leverage a custom Maven
repository link, although it needs a username and password. It also supports a custom proxy host and
port for internally allowed proxy servers.

`robolectric.dependency.repo.url` and `robolectric.dependency.repo.id` are enough for most
scenarios. For example, I often set the repository to a custom Chinese popular Maven mirror for my
custom projects:

```kotlin
testOptions {
    unitTests {
        includeAndroidResources = true
        returnDefaultValues = true
        isIncludeUnitTestDependencies = true
        all {
            it.systemProperty("robolectric.dependency.repo.url", "https://maven.aliyun.com/repository")
            it.systemProperty("robolectric.dependency.repo.id", "public")
        }
    }
}
```

[Robolectric's configuration documentation][robolectric-system-properties] contains a detailed
description of these special Robolectric properties, and you can read it for more details.

## Loading android-all jars from a local directory

Robolectric can load android-all jars from a local directory instead of downloading them from the
network when running Robolectric tests. We can set the `robolectric.dependency.dir` system property
to that directory like the following snippet:

```kotlin
testOptions {
    unitTests {
        includeAndroidResources = true
        returnDefaultValues = true
        isIncludeUnitTestDependencies = true
        all {
            it.systemProperty(
                "robolectric.dependency.dir",
                "${rootDir}/robolectric-jars/preinstrumented"
            )
        }
    }
}
```

When `robolectric.dependency.dir` is set, [Robolectric][legacy-dependency-resolver] always looks for
android-all jars in that directory and never downloads them, regardless of the value of
`robolectric.offline`. If a jar is missing from the directory, the tests fail with a
`Path is not a file: ...` error. So if only some environments have these jars, such as CI machines
with a pre-populated cache, only set `robolectric.dependency.dir` in those environments.
`robolectric.offline` is only useful when `robolectric.dependency.dir` is not set: setting it to
`true` makes Robolectric look for android-all jars in the test's working directory instead.

To make it work, we need to download android-all jars into the
`${rootDir}/robolectric-jars/preinstrumented` directory before running any Robolectric tests. I
created a sample project to provide build scripts to download these android-all jars into this
preinstrumented directory: [robolectric-offline-sample][robolectric-offline-sample].

```kotlin
plugins {
    `java-library`
}

val versions = listOf(
    "14-robolectric-10818077-i4",
    "13-robolectric-9030017-i4",
    "12.1-robolectric-8229987-i4",
    "12-robolectric-7732740-i4",
    "11-robolectric-6757853-i4",
    "10-robolectric-5803371-i4",
    "9-robolectric-4913185-2-i4",
    "8.1.0-robolectric-4611349-i4",
    "8.0.0_r4-robolectric-r1-i4",
    "7.1.0_r7-robolectric-r1-i4",
    "7.0.0_r1-robolectric-r1-i4",
    "6.0.1_r3-robolectric-r1-i4",
    "5.1.1_r9-robolectric-r2-i4",
    "5.0.2_r3-robolectric-r0-i4",
    "4.4_r1-robolectric-r2-i4"
)

val downloadTasks = versions.map { version ->
    val configurationName = "robolectric$version".replace(".", "_").replace("-", "_")
    val customConfiguration = configurations.create(configurationName) {
        extendsFrom(configurations.implementation.get())
        isCanBeResolved = true
        isCanBeConsumed = false
    }

    dependencies {
        add(configurationName, "org.robolectric:android-all-instrumented:$version")
    }

    val jarFileDirectory = customConfiguration.resolve().map { it.parentFile.absolutePath }
    val allFilesInDirectory = jarFileDirectory.flatMap { fileTree(it).files }

    val downloadTask = tasks.register<Copy>("downloadRobolectricJars$version") {
        from(allFilesInDirectory)
        into("preinstrumented")
    }

    downloadTask
}


val deleteTask = tasks.register<Delete>("deleteRobolectricJars") { delete("preinstrumented") }

tasks.register("downloadAllRobolectricJars") {
    dependsOn(deleteTask)
    dependsOn(downloadTasks)
}
```

The above `build.gradle.kts` is just a sample to download necessary android-all jars manually before
running Robolectric tests. It's easy to maintain. Because Robolectric might add a new android-all
jar for a new Android version or modify internal logic to update an existing android-all jar's
version, these android-all jars might change across different Robolectric versions. If you store
them in a Git repository, your Git repository might become bigger and bigger. If you like this
approach, you can store android-all jars in an external repository like [AndroidX][androidx].

[Robolectric's configuring documentation][robolectric-system-properties] contains a detailed
description of these special Robolectric properties, and you can read it for details.

## Fetching android-all jars manually before running Robolectric tests

If you don't store android-all jars in your Git repository for `robolectric.dependency.dir`, and you
don't want to modify your system properties for Robolectric in your `build.gradle.kts`, you can try
to download android-all jars in a script and download them manually before running any Gradle
tasks.

For example, I created a project to do it for myself:
[robolectric-android-all-fetcher][robolectric-android-all-fetcher]. You can change the Maven mirror
to any one you like and run the script to download all android-all jars for a specific Robolectric
version.

## Conclusion

Most of these issues are caused by a network issue when downloading necessary android-all jars, and
we can fix them or ease them by making android-all jars accessible before running Robolectric tests
and letting `MavenArtifactFetcher` use them directly. The above potential solutions are some stable
and recommended solutions for developers to try. Hope it can help you.

[androidx]: https://android-review.googlesource.com/c/platform/prebuilts/androidx/external/+/2813314
[legacy-dependency-resolver]: https://github.com/robolectric/robolectric/blob/6b50d7e1d9255f601c65062858a6daf2016deeac/robolectric/src/main/java/org/robolectric/plugins/LegacyDependencyResolver.java#L72-L77
[maven-artifact-fetcher]: https://github.com/robolectric/robolectric/blob/7fa0183c592974c3a84e948605f5278addae2731/plugins/maven-dependency-resolver/src/main/java/org/robolectric/internal/dependency/MavenArtifactFetcher.java#L37
[robolectric-android-all-fetcher]: https://github.com/utzcoz/robolectric-android-all-fetcher
[robolectric-issue-7886]: https://github.com/robolectric/robolectric/issues/7886
[robolectric-issue-8158]: https://github.com/robolectric/robolectric/issues/8158
[robolectric-issue-8205]: https://github.com/robolectric/robolectric/issues/8205
[robolectric-issue-11402]: https://github.com/robolectric/robolectric/issues/11402
[robolectric-offline-sample]: https://github.com/utzcoz/robolectric-offline-sample
[robolectric-system-properties]: ../../configuring.md#system-properties
