package apiKata.putProducts

import io.gatling.core.Predef._
import config.MethodsHttp.put
import utilities.{BuildRequest, EnvironmentValues, RequestParams}
import scala.util.Random

class PutProductsSuccess extends Simulation{

  val env = new EnvironmentValues()
  val params = new RequestParams()

  params.baseUrl = "https://fakestoreapi.com"
  params.pathUrl = "/products/5"

  val jsonPath = "data/dataput.json"

  params.headers = Map(
    "Content-Type" -> "application/json"
  )

  params.templateJson = jsonPath
  params.method = put
  params.requestName = "put model"
  params.statusExpected = 200
  params.scenarioName = "Modificación del producto exitosamente"
  params.id = Random.nextInt(20).toString

  val outputPath = "src/test/scala/apiKata/putProducts"
  val request = new BuildRequest(params, outputPath)

  val scenario1 = scenario("Modelo Abierto | Usuarios constantes por segundo").exec(request.scn).inject(constantUsersPerSec(4) during(2))
  val scenario2 = scenario("Modelo Abierto | Usuarios fijos al mismo tiempo").exec(request.scn).inject(atOnceUsers(3))
  val scenario3 = scenario("Modelo Abierto | Disminucion de usuarios").exec(request.scn).inject(rampUsersPerSec(7) to (2) during(2))
  val scenario4 = scenario("Modelo Cerrado | Usuarios concurrentes por segundo").exec(request.scn).inject(constantConcurrentUsers(3) during(3))
  val scenario5 = scenario("Modelo Cerrado | Aumento de usuarios concurrentes").exec(request.scn).inject(rampConcurrentUsers(1) to (5) during(5))

  setUp(
    scenario1, scenario2, scenario3, scenario4, scenario5
      .protocols(request.httpProtocol)
  )
}
