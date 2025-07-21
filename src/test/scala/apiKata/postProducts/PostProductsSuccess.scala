package apiKata.postProducts

import io.gatling.core.Predef._
import config.MethodsHttp.post
import utilities.{BuildRequest, RequestParams}

class PostProductsSuccess extends Simulation {

  val params = new RequestParams()

  params.baseUrl = "https://reqres.in"
  params.pathUrl = "/api/users"

  val jsonPath = "data/datapost.json"

  params.headers = Map(
    "x-api-key" -> "reqres-free-v1"
  )

  params.templateJson = jsonPath
  params.method = post
  params.requestName = "post model"
  params.statusExpected = 201
  params.scenarioName = "Creacion del usuario exitosamente"

  val outputPath = "src/test/scala/apiKata/postProducts"
  val request = new BuildRequest(params, outputPath)

  val scenario1 = scenario("Modelo Abierto | Usuarios constantes por segundo").exec(request.scn).inject(constantUsersPerSec(10) during(20))

  setUp(
    scenario1
  ).protocols(request.httpProtocol)
}