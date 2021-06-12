import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.5.10"
  kotlin("plugin.spring") version "1.5.10"
  id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
}

group = "com.miya10kei"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

dependencies {
  @Suppress("LocalVariableName") val SPRING_BOOT_VERSION = "2.5.1"
  implementation(platform("org.springframework.boot:spring-boot-dependencies:$SPRING_BOOT_VERSION"))
  annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:$SPRING_BOOT_VERSION"))

  implementation("org.springframework.boot:spring-boot-autoconfigure")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  compileOnly("org.springframework.boot:spring-boot-starter-web")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "1.8"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

ktlint {
  version.set("0.41.0")
  outputColorName.set("RED")
  additionalEditorconfigFile.set(File(".editorconfig"))
}
