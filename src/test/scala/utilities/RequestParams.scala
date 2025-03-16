package utilities

class RequestParams {
  var baseUrl: String = ""
  var pathUrl: String = ""
  var queryParams: Map[String, String] = Map()
  var headers: Map[String, String] = Map()
  var method: String = ""
  var requestName: String = ""
  var scenarioName: String = ""
  var statusExpected: Int = 0
  var feederCsv: Array[String] = Array()
  var feederRandom: Map[String, Any] = Map()
  var templateJson: String = ""
  var body: String = ""
  var id: String = ""
}
