---
date: 2017-03-01
authors:
  - xian
hide:
  - toc
slug: hermetic-builds
---

# Hermetic Builds with Robolectric

Robolectric needs access to multiple Android SDK jars in order to perform its magic, which means it needs special configuration beyond just setting up dependencies in your build. By default, it tries to download Android SDK jars from Maven Central.

But what if you have a [hermetic build environment](http://blog.fahhem.com/2013/12/hermetic-build-systems/)? You just need to do a little more configuration.

Here's a Gradle build script that'll help:

```groovy
def robolectricVersion = '3.3'

def androidSdkVersions = [
        '4.1.2_r1-robolectric-0',
        '4.2.2_r1.2-robolectric-0',
        '4.3_r2-robolectric-0',
        '4.4_r1-robolectric-1',
        '5.0.0_r2-robolectric-1',
        '5.1.1_r9-robolectric-1',
        '6.0.0_r1-robolectric-0',
        '6.0.1_r3-robolectric-0',
        '7.0.0_r1-robolectric-0',
        '7.1.0_r7-robolectric-0',
]

def shadowArtifacts = [
        "org.robolectric:shadows-core:${robolectricVersion}",
        "org.robolectric:shadows-httpclient:${robolectricVersion}",
        "org.robolectric:shadows-maps:${robolectricVersion}",
        "org.robolectric:shadows-multidex:${robolectricVersion}",
        "org.robolectric:shadows-play-services:${robolectricVersion}",
        "org.robolectric:shadows-support-v4:${robolectricVersion}",
]

apply plugin: 'java'

repositories {
    mavenLocal()
    jcenter()
}

configurations {
    sandbox
}

def allSdkConfigurations = []

androidSdkVersions.forEach { version ->
    allSdkConfigurations << configurations.create(version)
    dependencies.add(version, "org.robolectric:android-all:${version}")
    dependencies.add('sandbox', "org.robolectric:android-all:${version}")
}


// In this section you declare the dependencies for your production and test code
dependencies {
    compile("org.robolectric:robolectric:${robolectricVersion}") {
        // we don't need these MavenDependencyResolver in a hermetic build
        exclude group: 'org.apache.maven', module: 'maven-ant-tasks'
        exclude group: 'org.apache.ant', module: 'ant'
    }

    shadowArtifacts.forEach { shadowArtifact ->
        compile shadowArtifact
        sandbox shadowArtifact
    }
}


task createRobolectricDeps {
}

task copyLibs(type: Copy) {
    into "$buildDir/output/libs"
    from configurations.compile

    doLast {
        def f = new File("$buildDir/output/README.txt")
        f.delete()

        f << "# Include the following jar files on your classpath:\n"
        f << "#\n"

        source.forEach { file ->
            f << "libs/${file.name}\n"
        }
    }
}

task copySdks(type: Copy) {
    into "$buildDir/output/libs"
    from allSdkConfigurations

    doLast {
        def f = new File("$buildDir/output/robolectric-deps.properties")
        f.delete()

        f << "# Place this file in your test resources dir (e.g. src/test/resources).\n"
        f << "# Paths below should be absolute, or relative to this file.\n"
        f << "#\n"

        allSdkConfigurations.forEach { config ->
            config.allDependencies.forEach { dep ->
                def files = new ArrayList(config.files)
                if (files.size != 1) {
                    throw new RuntimeException("huh, more than one file in ${dep}? ${files}")
                }
                def file = files[0]
                f << "${dep.group}\\:${dep.name}\\:${dep.version}=path/to/${file.name}\n"
            }
        }
    }
}

task filesForHermeticBuild {
    dependsOn createRobolectricDeps
    dependsOn copyLibs
    dependsOn copySdks
}
```

1. Create an empty Gradle project (either `gradle init` or use Android Studio or IntelliJ).
1. Paste the script into your `build.gradle`.
1. Change the first line (`robolectricVersion`) to the version of Robolectric you want.
1. Change the list of `shadowArtifacts` or `androidSdkVersions` if you like.
1. Run the `filesForHermeticBuild` task: `./gradlew filesForHermeticBuild`

Gradle will download all the dependencies you need to run Robolectric and place them in `build/output/libs`. Place the `.jar` files in your project's libs directory.

Add the `.jar` files listed in `build/output/README.txt` as compile-time dependencies.

Place the file called `build/output/robolectric-deps.properties` in your test resources directory. Change the paths as indicated in the comment in that file.

You're all set! Robolectric will now load Android SDKs from the filesystem instead of attempting to download them from Maven Central.
