plugins {
  kotlin("jvm").version("1.7.10")
  id("com.github.johnrengelman.shadow").version("7.1.2")
}

group = "me.backword"
version = "3"

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation("com.fasterxml.jackson.core:jackson-core:2.13.3")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "17"
  }

  shadowJar {
    manifest.attributes["Main-Class"] = "me.backword.MCServerCreatorKt"
    archiveFileName.set("MCServerCreator.jar")
  }
}