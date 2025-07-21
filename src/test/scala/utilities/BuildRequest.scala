package utilities

import java.io.PrintWriter
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.http.request.builder.HttpRequestBuilder

import play.api.libs.json._  // Play JSON

import scala.collection.mutable.ListBuffer

class BuildRequest(requestParams: RequestParams, outputPath: String = "responses") {

  // Buffer para acumular todas las respuestas JSON
  private val responsesBuffer = ListBuffer.empty[JsObject]

  def httpProtocol: HttpProtocolBuilder = http
    .baseUrl(requestParams.baseUrl)

  def httpRequest: HttpRequestBuilder = {
    var requestBuild = http(requestParams.requestName)
      .httpRequest(requestParams.method, requestParams.pathUrl)
      .check(bodyString.saveAs("responseBody"))
      .check(status.saveAs("responseStatus"))
      .queryParamMap(requestParams.queryParams)
      .check(status.is(requestParams.statusExpected))
      .headers(requestParams.headers)

    if (requestParams.templateJson != "") {
      requestBuild = requestBuild.body(ElFileBody(requestParams.templateJson)).asJson
    }
    requestBuild
  }

  // Guarda en memoria cada respuesta (JSON)
  private def saveResponseInMemory(session: Session): Session = {
    val responseStatus = session("responseStatus").as[Int]
    val responseBody = session("responseBody").as[String]
    val requestName = requestParams.requestName

    val bodyJsonValue: JsValue = try {
      Json.parse(responseBody)
    } catch {
      case _: Throwable => JsString(responseBody)
    }

    val jsonEntry = Json.obj(
      "requestName" -> requestName,
      "status" -> responseStatus,
      "body" -> bodyJsonValue
    )

    responsesBuffer.synchronized {
      responsesBuffer += jsonEntry
    }

    // Imprime resumen en consola
    if (responseStatus == requestParams.statusExpected) {
      println(s"[OK] $requestName - Status: $responseStatus")
    } else {
      println(s"[FAIL] $requestName - Status: $responseStatus")
    }

    session
  }

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
    }
  }

  def scn: ScenarioBuilder = {
    var scenarioRequest = scenario(requestParams.scenarioName)
    for (i <- requestParams.feederCsv.indices) {
      scenarioRequest = scenarioRequest.feed(csv(requestParams.feederCsv(i)).queue)
    }
    scenarioRequest = scenarioRequest.feed(Iterator.continually {
      requestParams.feederRandom
    })
    scenarioRequest = scenarioRequest
      .exec(httpRequest)
      .exec { session =>
        saveResponseInMemory(session)
        session
      }
    scenarioRequest
  }
}
