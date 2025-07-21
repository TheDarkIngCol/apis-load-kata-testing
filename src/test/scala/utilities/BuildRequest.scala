package utilities

import java.io.PrintWriter
import java.nio.file.{Files, Paths}
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.http.request.builder.HttpRequestBuilder

<<<<<<< HEAD
import play.api.libs.json._

import scala.collection.mutable.ListBuffer

class BuildRequest(requestParams: RequestParams, outputPath: String = "") {

=======
import play.api.libs.json._  // Play JSON

import scala.collection.mutable.ListBuffer

class BuildRequest(requestParams: RequestParams, outputPath: String = "responses") {

  // Buffer para acumular todas las respuestas JSON
>>>>>>> main
  private val responsesBuffer = ListBuffer.empty[JsObject]

  def httpProtocol: HttpProtocolBuilder = http
    .baseUrl(requestParams.baseUrl)

  def httpRequest: HttpRequestBuilder = {
    var request = http(requestParams.requestName)
      .httpRequest(requestParams.method, requestParams.pathUrl)
<<<<<<< HEAD
      .queryParamMap(requestParams.queryParams)
=======
      .check(bodyString.saveAs("responseBody"))
      .check(status.saveAs("responseStatus"))
      .queryParamMap(requestParams.queryParams)
      .check(status.is(requestParams.statusExpected))
>>>>>>> main
      .headers(requestParams.headers)
      .check(status.is(requestParams.statusExpected))
      .check(status.saveAs("responseStatus"))
      .check(bodyString.saveAs("responseBody"))

<<<<<<< HEAD
    if (requestParams.templateJson.nonEmpty) {
      request = request.body(ElFileBody(requestParams.templateJson)).asJson
=======
    if (requestParams.templateJson != "") {
      requestBuild = requestBuild.body(ElFileBody(requestParams.templateJson)).asJson
>>>>>>> main
    }
    request
  }

<<<<<<< HEAD
  private def saveResponseInMemory(session: Session): Session = {
    val responseStatus = session("responseStatus").as[Int]
    val responseBody   = session("responseBody").as[String]
    val requestName    = requestParams.requestName
=======
  // Guarda en memoria cada respuesta (JSON)
  private def saveResponseInMemory(session: Session): Session = {
    val responseStatus = session("responseStatus").as[Int]
    val responseBody = session("responseBody").as[String]
    val requestName = requestParams.requestName
>>>>>>> main

    val bodyJsonValue: JsValue = try {
      Json.parse(responseBody)
    } catch {
      case _: Throwable => JsString(responseBody)
    }

    val jsonEntry = Json.obj(
      "requestName" -> requestName,
<<<<<<< HEAD
      "status"      -> responseStatus,
      "body"        -> bodyJsonValue
=======
      "status" -> responseStatus,
      "body" -> bodyJsonValue
>>>>>>> main
    )

    responsesBuffer.synchronized {
      responsesBuffer += jsonEntry
    }
<<<<<<< HEAD
=======

    // Imprime resumen en consola
    if (responseStatus == requestParams.statusExpected) {
      println(s"[OK] $requestName - Status: $responseStatus")
    } else {
      println(s"[FAIL] $requestName - Status: $responseStatus")
    }
>>>>>>> main

    session
  }

<<<<<<< HEAD
  private def saveAllResponsesToFile(): Unit = {
    val targetDir = if (outputPath.trim.isEmpty) {
      Paths.get("").toAbsolutePath.toString
    } else outputPath

    val dirPath = Paths.get(targetDir)
    if (!Files.exists(dirPath)) {
      Files.createDirectories(dirPath)
    }

    val file = dirPath.resolve("all_responses.json").toFile

    val jsonArray = Json.toJson(responsesBuffer.toList)
    val writer = new PrintWriter(file)

    try {
      writer.write(Json.prettyPrint(jsonArray))
      println(s"Archivo JSON con todas las respuestas guardado en: ${file.getAbsolutePath}")
    } finally {
      writer.close()
=======
  // Guarda todas las respuestas acumuladas en un archivo JSON al final
  def saveAllResponsesToFile(): Unit = {
    val file = new java.io.File(s"$outputPath/all_responses.json")
    file.getParentFile.mkdirs() // crea carpeta si no existe
    val jsonArray = Json.toJson(responsesBuffer.toList)
    val pw = new PrintWriter(file)
    try {
      pw.write(Json.prettyPrint(jsonArray))
      println(s"Archivo JSON con todas las respuestas guardado en: ${file.getAbsolutePath}")
    } finally {
      pw.close()
>>>>>>> main
    }
  }

  def scn: ScenarioBuilder = {
    var scenarioRequest = scenario(requestParams.scenarioName)

    requestParams.feederCsv.foreach { feeder =>
      scenarioRequest = scenarioRequest.feed(csv(feeder).queue)
    }

    scenarioRequest = scenarioRequest.feed(Iterator.continually {
      requestParams.feederRandom
    })

    scenarioRequest = scenarioRequest
      .exec(httpRequest)
<<<<<<< HEAD
      .exec(session => saveResponseInMemory(session))

    scenarioRequest = scenarioRequest.exec { session =>
      saveAllResponsesToFile()
      session
    }

=======
      .exec { session =>
        saveResponseInMemory(session)
        session
      }
>>>>>>> main
    scenarioRequest
  }
}
