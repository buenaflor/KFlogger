import java.net.URI

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = URI("https://androidx.dev/kmp/builds/10477498/artifacts/snapshots/repository") }
    }
}

rootProject.name = "KFlogger"

include(":api")
