import org.jetbrains.kotlin.gradle.internal.KaptWithoutKotlincTask
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.errorprone)
  kotlin("kapt")
  `maven-publish`
}

group = properties["groupName"].toString()

version = properties["versionName"].toString()

kotlin {
  jvm {
    withJava()
    jvmToolchain(11)
  }

  listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
    it.binaries.framework { baseName = "shared" }
  }

  sourceSets {
    val commonMain by getting
    val commonTest by getting { dependencies { implementation(kotlin("test")) } }
    val jvmMain by getting {
      dependencies {
        implementation(libs.checker)
        implementation(libs.checker.compat.qual)
        implementation(files("build/libs/stack_getter_java_lang_access_impl.jar"))
        errorprone(libs.errorprone.core.get())
      }
    }
    val jvmTest by getting {
      dependencies {
        implementation(libs.truth)
        implementation(libs.mockito.core)
        implementation(libs.guava.jre)
        configurations["kaptTest"].dependencies.add(implementation(libs.auto.service.get()))
        implementation(libs.auto.service)
      }
    }
    val iosX64Main by getting
    val iosArm64Main by getting
    val iosSimulatorArm64Main by getting
    val iosMain by creating {
      dependsOn(commonMain)
      iosX64Main.dependsOn(this)
      iosArm64Main.dependsOn(this)
      iosSimulatorArm64Main.dependsOn(this)
    }
    val iosX64Test by getting
    val iosArm64Test by getting
    val iosSimulatorArm64Test by getting
    val iosTest by creating {
      dependsOn(commonTest)
      iosX64Test.dependsOn(this)
      iosArm64Test.dependsOn(this)
      iosSimulatorArm64Test.dependsOn(this)
    }
  }
}

val checksSources = listOf("src/jvmMain/java/com/buenaflor/kflogger/util/Checks.java")

val stackGetterCommonSources =
    listOf(
        "src/jvmMain/java/com/buenaflor/kflogger/util/StackGetter.java",
        "src/jvmMain/java/com/buenaflor/kflogger/util/ThrowableStackGetter.java")

val stackGetterCommon: Configuration by configurations.creating { isCanBeResolved = true }

val javaLangAccessStackGetter by
    tasks.registering(JavaCompile::class) {
      sourceCompatibility = "8"
      targetCompatibility = "8"
      classpath = stackGetterCommon + files(configurations.getByName("errorprone"))
      destinationDirectory.set(file("$buildDir/classes/java"))
      include("**/StackGetter.java")
      include("**/ThrowableStackGetter.java")
      include("**/Checks.java")
      include("**/JavaLangAccessStackGetter.java")
      source(
          stackGetterCommonSources +
              checksSources +
              "src/jvmMain/java/com/buenaflor/kflogger/util/JavaLangAccessStackGetter.java")
    }

val generateStackGetterJavaLangAccessImpl by
    tasks.registering(Jar::class) {
      archiveClassifier.set("")
      from(javaLangAccessStackGetter.get().destinationDirectory)
      archiveFileName.set("stack_getter_java_lang_access_impl.jar")
      dependsOn(javaLangAccessStackGetter)
    }

tasks.named("compileJava", JavaCompile::class) {
  exclude("**/JavaLangAccessStackGetter.java")
  exclude(checksSources)
  exclude(stackGetterCommonSources)
  dependsOn(generateStackGetterJavaLangAccessImpl)
}

tasks.withType<KaptWithoutKotlincTask> { mustRunAfter(generateStackGetterJavaLangAccessImpl) }

/**
 * Task for removing service files from the build directory produced by @AutoService during tests.
 */
private val deleteServiceFilesFromBuildDir by
    tasks.registering(Delete::class) {
      delete(
          fileTree(buildDir) {
            include("**/*backend.system.BackendFactory")
            include("**/*context.ContextDataProvider")
          })
    }

/**
 * Task for running tests from the FluentLoggerTest.java file.
 *
 * It depends on deleteServiceFilesFromBuildDir in order to remove service files that persist during
 * DefaultPlatformServiceLoadingTest. If not deleted, this will automatically try to load the
 * services that have been injected during DefaultPlatformServiceLoadingTest in the FluentLoggerTest
 * which causes that test to fail.
 *
 * This task has to be executed after all the other tests have finished.
 */
private val jvmFluentLoggerTest by
    tasks.registering(Test::class) {
      filter { includeTestsMatching("*FluentLoggerTest.testCreate") }
      dependsOn("deleteBuildFilesForSpecificTest")
    }

tasks.named<Test>("jvmTest") {
  filter { excludeTestsMatching("com.buenaflor.kflogger.FluentLoggerTest.testCreate") }
  finalizedBy(jvmFluentLoggerTest)
}

fun KotlinDependencyHandler.errorprone(dependencyNotation: Any): Dependency? {
  val dependency = implementation(dependencyNotation)
  return dependency?.let {
    project.configurations.getByName("errorprone").dependencies.add(dependency)
    it
  }
}

publishing {
  // TODO: Create a separate publication for "system-backend"
  publications {
    // Define the publication with the desired artifact name
    create<MavenPublication>("KFlogger") {
      artifactId = "kflogger"
      groupId = group.toString()
      version = version.toString()

      from(components["kotlin"])
    }
  }

  repositories {
    // Configure the Maven repository where we want to publish
    mavenLocal()
  }
}
