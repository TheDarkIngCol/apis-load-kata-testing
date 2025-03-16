package apiKata.getProducts

import io.gatling.core.Predef._
import config.MethodsHttp.get
import utilities.{BuildRequest, EnvironmentValues, RequestParams}

class GetProductsSuccess extends Simulation {

  val env = new EnvironmentValues()
  val params = new RequestParams()

  params.baseUrl = "https://fakestoreapi.com"
  params.pathUrl = "/products"

  params.headers = Map(
    "Content-Type" -> "application/json"
  )

  params.method = get
  params.requestName = "get model"
  params.statusExpected = 200
  params.scenarioName = "Obtención del producto exitosamente"

  val outputPath = "src/test/scala/apiKata/getProducts"
  val request = new BuildRequest(params, outputPath)

  val scenario1 = scenario("Modelo Abierto | Usuarios constantes por segundo").exec(request.scn).inject(constantUsersPerSec(3) during(2))
  val scenario2 = scenario("Modelo Abierto | Usuarios fijos al mismo tiempo").exec(request.scn).inject(atOnceUsers(3))
  val scenario3 = scenario("Modelo Abierto | Disminucion de usuarios").exec(request.scn).inject(rampUsersPerSec(5) to (2) during(10))
  val scenario4 = scenario("Modelo Cerrado | Usuarios concurrentes por segundo").exec(request.scn).inject(constantConcurrentUsers(5) during(3))
  val scenario5 = scenario("Modelo Cerrado | Aumento de usuarios concurrentes").exec(request.scn).inject(rampConcurrentUsers(3) to (6) during(5))

  setUp(
    scenario1, scenario2, scenario3, scenario4, scenario5
  ).protocols(request.httpProtocol)
}
