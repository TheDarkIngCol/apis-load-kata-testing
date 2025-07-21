package utilities

import java.io.PrintWriter
import java.nio.file.{Files, Paths}

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.http.request.builder.HttpRequestBuilder

import play.api.libs.json._

import scala.collection.mutable.ListBuffer

class BuildRequest(requestParams: RequestParams, outputPath: String = "") {

  private val responsesBuffer = ListBuffer.empty[JsObject]

  def httpProtocol: HttpProtocolBuilder = http
    .baseUrl(requestParams.baseUrl)

  def httpRequest: HttpRequestBuilder = {
    var request = http(requestParams.requestName)
      .httpRequest(requestParams.method, requestParams.pathUrl)
      .queryParamMap(requestParams.queryParams)
      .headers(requestParams.headers)
      .check(status.is(requestParams.statusExpected))
      .check(status.saveAs("responseStatus"))
      .check(bodyString.saveAs("responseBody"))

    if (requestParams.templateJson.nonEmpty) {
      request = request.body(ElFileBody(requestParams.templateJson)).asJson
    }
    request
  }

  private def saveResponseInMemory(session: Session): Session = {
    val responseStatus = session("responseStatus").as[Int]
    val responseBody   = session("responseBody").as[String]
    val requestName    = requestParams.requestName

    val bodyJsonValue: JsValue = try {
      Json.parse(responseBody)
    } catch {
      case _: Throwable => JsString(responseBody)
    }

    val jsonEntry = Json.obj(
      "requestName" -> requestName,
      "status"      -> responseStatus,
      "body"        -> bodyJsonValue
    )

    responsesBuffer.synchronized {
      responsesBuffer += jsonEntry
    }

    session
  }

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
    } finally {
      writer.close()
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
      .exec(session => saveResponseInMemory(session))
      .exec { session =>
        saveAllResponsesToFile()
        session
      }

    scenarioRequest
  }
}