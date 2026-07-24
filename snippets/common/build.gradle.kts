plugins {
  alias(libs.plugins.android.library)
}

android {
  namespace = "org.robolectric.snippets.common"
  compileSdk = 37

  defaultConfig {
    minSdk = 23
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}
