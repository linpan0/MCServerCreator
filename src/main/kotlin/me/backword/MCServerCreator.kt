package me.backword

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.net.URLDecoder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

private class MCServerCreator {
  companion object {
    private val MAPPER = ObjectMapper()
    private const val VERSIONS_CURL = "curl -X GET \"https://api.papermc.io/v2/projects/paper\""
    private const val BUILDS_CURL = "curl -X GET \"https://api.papermc.io/v2/projects/paper/versions/%s/builds\""
    private const val DOWNLOAD_CURL = "curl -X GET \"https://api.papermc.io/v2/projects/paper/versions/%s/builds/%s/downloads/%s\" -o \"%s\""
  }

  fun start() {
    val version = queryVersion()
    val latestBuild = latestBuild(version)
    val fileDir = queryFileDir()
    downloadJar(version, latestBuild, fileDir)
    copyFile("eula.txt", Path.of(fileDir, "eula.txt"))
    copyFile("run.bat", Path.of(fileDir, "run.bat"))
    copyFile("run.sh", Path.of(fileDir, "run.sh"))
  }

  private fun queryVersion(): String {
    while (true) {
      print("Please enter a version (${versions().joinToString()}): ")
      val version = readlnOrNull() ?: continue
      if (!validateVersion(version)) continue
      return version
    }
  }

  private fun validateVersion(input: String) = versions().contains(input.replace(" ", ""))

  private fun queryFileDir(): String {
    while (true) {
      print("Please enter a file directory to set up the server (Uses the current one if empty): ")
      val inputDir = readlnOrNull() ?: continue
      return inputDir.ifEmpty {
        var dir = File(MCServerCreator::class.java.protectionDomain.codeSource.location.toURI()).parentFile.path
        dir = URLDecoder.decode(dir, "UTF-8")
        return dir
      }
    }
  }

  private fun versions() =
    MAPPER.readValue(MAPPER.readTree(run(VERSIONS_CURL)).at("/versions").toString(), Array<String>::class.java)

  private fun latestBuild(version: String): Int {
    val builds = MAPPER.readTree(run(BUILDS_CURL.format(version))).get("builds")
    return builds.get(builds.size() - 1).first().asInt()
  }

  private fun downloadJar(version: String, build: Int, fileDir: String) =
    run(DOWNLOAD_CURL.format(version, build, "paper-${version}-${build}.jar", "${fileDir}${File.separator}paper.jar"))

  private fun copyFile(name: String, destination: Path) {
    destination.toFile().parentFile.mkdirs()
    MCServerCreator::class.java.getResourceAsStream("/$name").use {
      Files.copy(it!!, destination, StandardCopyOption.REPLACE_EXISTING)
    }
  }

  private fun run(cmd: String) = Runtime.getRuntime().exec(cmd).inputReader()
}

fun main() = MCServerCreator().start()