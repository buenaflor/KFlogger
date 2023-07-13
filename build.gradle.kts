import com.diffplug.spotless.LineEnding.UNIX

plugins {
  // trick: for the same plugin versions in all sub-modules
  alias(libs.plugins.kotlin.multiplatform).apply(false)
  alias(libs.plugins.spotless)
}

spotless {
  lineEndings = UNIX

  kotlin {
    target("**/*.kt")
    ktfmt()
  }
  kotlinGradle {
    target("**/*.kts")
    ktfmt()
  }
}
