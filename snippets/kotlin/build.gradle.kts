import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
  alias(libs.plugins.android.application)
}

android {
  namespace = "org.robolectric.snippets.kotlin"
  compileSdk = 37

  defaultConfig {
    applicationId = "org.robolectric.snippets.kotlin"
    minSdk = 23
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  testOptions {
    unitTests.isIncludeAndroidResources = true
  }
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

dependencies {
  implementation(project(":common"))

  testImplementation(libs.androidx.test.ext.junit)
  testImplementation(libs.junit)
  testImplementation(libs.robolectric)
}

tasks.withType<Test>().configureEach {
  testLogging.exceptionFormat = TestExceptionFormat.FULL
}
