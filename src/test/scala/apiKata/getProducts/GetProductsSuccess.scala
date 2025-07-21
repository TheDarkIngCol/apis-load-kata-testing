package apiKata.getProducts

import io.gatling.core.Predef._
import config.MethodsHttp.get
import utilities.{BuildRequest, RequestParams}

class
GetProductsSuccess extends Simulation {

  val params = new RequestParams()

  params.baseUrl = "https://reqres.in"
  params.pathUrl = "/api/users?page=2"


  params.headers = Map(
    "x-api-key" -> "reqres-free-v1"
  )

  params.method = get
  params.requestName = "get model"
  params.statusExpected = 200
  params.scenarioName = "Obtención de usuarios exitosamente"

  val outputPath = "src/test/scala/apiKata/getProducts"
  val request = new BuildRequest(params, outputPath)

  val scenario1 = scenario("Modelo Abierto | Usuarios constantes por segundo").exec(request.scn).inject(constantUsersPerSec(200) during(30));

  setUp(
    scenario1
  ).protocols(request.httpProtocol)
}
