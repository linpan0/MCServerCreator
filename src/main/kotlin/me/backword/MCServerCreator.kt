package me.backword

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import java.io.File
import java.net.URLDecoder
import java.nio.file.Files
import java.nio.file.Path

private class MCServerCreator {}

private val MAPPER = ObjectMapper()
private const val VERSIONS_CURL = "curl -X GET \"https://api.papermc.io/v2/projects/paper\""
private const val BUILDS_CURL = "curl -X GET \"https://api.papermc.io/v2/projects/paper/versions/%s/builds\""
private const val DOWNLOAD_CURL =
  "curl -X GET \"https://api.papermc.io/v2/projects/paper/versions/%s/builds/%s/downloads/%s\" -o \"%s\""

fun main() {
  val version = queryVersion()
  val latestBuild = latestBuild(version)
  val fileDir = queryFileDir()
  downloadJar(version, latestBuild, fileDir)
  copyFile("eula.txt", Path.of(fileDir, "eula.txt"))
  copyFile("run.bat", Path.of(fileDir, "run.bat"))
}

private fun queryVersion(): String {
  while (true) {
    print("Please enter a version (${versions().joinToString()}): ")
    val version = readLine() ?: continue;
    if (!validateVersion(version)) continue;
    return version
  }
}

private fun queryFileDir(): String {
  while (true) {
    print("Please enter a file directory to set up the server (Uses the current one if empty): ")
    val inputDir = readLine() ?: continue;
    return inputDir.ifEmpty {
      var dir = File(MCServerCreator::class.java.protectionDomain.codeSource.location.toURI()).parentFile.path
      dir = URLDecoder.decode(dir, "UTF-8")
      return dir
    }
  }
}

private fun validateVersion(input: String) = versions().contains(input.replace(" ", ""))

private fun versions(): Array<String> {
  val jsonNode = MAPPER.readTree(run(VERSIONS_CURL))
  return MAPPER.readValue(jsonNode.at("/versions").toString(), Array<String>::class.java)
}

@Suppress("UNCHECKED_CAST")
private fun latestBuild(version: String): Int {
  val buildsNode = MAPPER.readTree(run(BUILDS_CURL.format(version)))
  val builds = buildsNode.get("builds")
  return builds.get(builds.size() - 1).first().asInt()
}

private fun downloadJar(version: String, build: Int, fileDir: String) =
  run(DOWNLOAD_CURL.format(version, build, "paper-${version}-${build}.jar", "${fileDir}${File.separator}paper.jar"))

private fun copyFile(name: String, destination: Path) {
  MCServerCreator::class.java.getResourceAsStream("/$name").use {
    Files.copy(it!!, destination)
  }
}

private fun run(cmd: String) = Runtime.getRuntime().exec(cmd).inputReader()