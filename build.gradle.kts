plugins {
  kotlin("jvm").version("1.9.22")
  id("com.github.johnrengelman.shadow").version("8.1.1")
}

group = "me.backword"
version = "4"

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
}

tasks {
  compileJava {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }

  compileKotlin {
    kotlinOptions.jvmTarget = "17"
  }

  shadowJar {
    manifest.attributes["Main-Class"] = "me.backword.MCServerCreatorKt"
    archiveFileName.set("MCServerCreator.jar")
  }
}