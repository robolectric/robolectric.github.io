// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.spotless)
}

spotless {
  // Add configurations for Java files
  java {
    target("**/*.java")
    googleJavaFormat("1.36.1")
  }

  // Add configurations for Kotlin files
  kotlin {
    target("**/*.kt")
    ktfmt("0.64").googleStyle()
  }

  // Add configurations for Kotlin Gradle files
  kotlinGradle {
    target("**/*.kts")
    ktfmt("0.64").googleStyle()
  }
}
