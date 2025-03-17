package utilities

import java.nio.charset.StandardCharsets
import java.io.PrintWriter
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import io.gatling.http.request.builder.HttpRequestBuilder

//clase que construye solicitudes HTTP para pruebas de carga.
class BuildRequest(requestParams: RequestParams, outputPath: String = "responses") {

  val env = new EnvironmentValues()
  val requestSigner = new RequestSignerAWS(requestParams)

  if (env.getenv("USE_LAMBDA_URL") == "true") {
    requestParams.baseUrl = requestSigner.url
    requestParams.pathUrl = ""
    requestParams.headers = Map()
    requestParams.method = requestSigner.method
    requestParams.templateJson = requestSigner.body
    requestParams.queryParams = Map()
  }

  def httpProtocol: HttpProtocolBuilder = http
    .baseUrl(requestParams.baseUrl)

  //Define la URL base para todas las solicitudes HTTP.
  def httpRequest: HttpRequestBuilder = {
    var requestBuild = http(requestParams.requestName)
      .httpRequest(requestParams.method, requestParams.pathUrl)
      .check(bodyString.saveAs("responseBody")) // Guardar el cuerpo de la respuesta
      .check(status.saveAs("responseStatus"))   // Guardar el status de la respuesta
      .queryParamMap(requestParams.queryParams)
      .check(status.is(requestParams.statusExpected)) // Validar el status esperado
      .headers(requestParams.headers)

    if (requestParams.templateJson != "") {
      if (env.getenv("USE_LAMBDA_URL") == "true") {
        requestBuild = requestBuild.body(StringBody(s"""${requestParams.templateJson}"""))
        requestBuild = requestBuild.sign { (request, session) =>
          val bodyJson = new String(request.getBody.getBytes, StandardCharsets.UTF_8)
          val headerSigned = requestSigner.headers(bodyJson)
          for ((k, v) <- headerSigned) request.getHeaders.set(k, v)
          request
        }
      } else {
        requestBuild = requestBuild.body(ElFileBody(requestParams.templateJson)).asJson
      }
    }
    requestBuild
  }

  // Método para guardar la respuesta en un archivo JSON y mostrar en consola
  private def saveResponseToFile(session: Session): Session = {
    // Obtener el status y el cuerpo de la respuesta
    val responseStatus = session("responseStatus").as[Int]
    val responseBody = session("responseBody").as[String]

    // Comparar el status encontrado con el status esperado
    if (responseStatus == requestParams.statusExpected) {
      println(s"Status exitoso: Esperado: ${requestParams.statusExpected}, Encontrado: $responseStatus")
    } else {
      println(s"Status Fallido: Esperado ${requestParams.statusExpected}, Encontrado $responseStatus")
    }

    // Mostrar en consola el cuerpo de la respuesta
    println(s"Response Body: $responseBody")

    // Guardar la respuesta en un archivo JSON con un nombre único
    val fileName = s"$outputPath/response_${requestParams.requestName}.json"
    val pw = new PrintWriter(fileName)
    pw.write(responseBody)
    pw.close()
    session
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
      .exec(httpRequest) // Ejecutar la solicitud HTTP
      .exec { session =>
        saveResponseToFile(session) // Guardar la respuesta en un archivo JSON y mostrar en consola
        session
      }
    scenarioRequest
  }
}