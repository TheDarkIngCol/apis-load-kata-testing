package utilities

import java.io.ByteArrayInputStream
import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.Paths.{get => getFile}
import org.json4s._
import org.json4s.native.Serialization._
import org.json4s.native.Serialization
import scala.io.{BufferedSource, Source}
import config.MethodsHttp.post
import com.amazonaws.DefaultRequest
import com.amazonaws.auth.AWS4Signer
import com.amazonaws.auth.AWSSessionCredentials
import com.amazonaws.http.HttpMethodName
import scala.collection.mutable
import scala.jdk.CollectionConverters.MapHasAsScala

class RequestSignerAWS(requestParams: RequestParams){

  val uriEndpoint: URI = URI.create("")
  val StringToChange: String = "PLACE_TO_SET_CUSTOM_BODY_JSON"
  val bodyRequestWithoutBody = Map("url" -> requestParams.baseUrl.concat(requestParams.pathUrl),
    "method" -> requestParams.method,
    "headers" -> requestParams.headers,
    "data" -> StringToChange,
    "params" -> requestParams.queryParams
  )

  def headers(content: String): mutable.Map[String, String] = {

    val request = new DefaultRequest[Void]("lambda")
    request.setHttpMethod(HttpMethodName.POST)
    request.setEndpoint(uriEndpoint)
    val contentBytes = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))
    request.setContent(contentBytes)
    val credentials = new AWSSessionCredentials() {
      def getSessionToken: String = System.getenv("AWS_SESSION_TOKEN")
      def getAWSAccessKeyId: String = System.getenv("AWS_ACCESS_KEY_ID")
      def getAWSSecretKey: String = System.getenv("AWS_SECRET_ACCESS_KEY")
    }
    val signer = new AWS4Signer
    signer.setRegionName("us-east-1")
    signer.setServiceName(request.getServiceName)
    signer.sign(request, credentials)
    request.addHeader("Content-Type", "application/json")
    val headersSigned = request.getHeaders.asScala
    headersSigned
  }
  def body: String = {
    implicit val formats: AnyRef with Formats = Serialization.formats(NoTypeHints)
    var bodyData: String = ""

    if(requestParams.templateJson != ""){
      val pathFile: String = getFile("src/main/resources", requestParams.templateJson).normalize().toString
      val sourceFile: BufferedSource = Source.fromFile(pathFile)
      bodyData = try sourceFile.mkString finally sourceFile.close()
    }

    val bodyRequest: String = write(bodyRequestWithoutBody)
      .replace(StringToChange, s"\"$bodyData\"")
      .replace("\"\"{", "{")
      .replace("}\"\"", "}")
      .replace("\"\"[", "[")
      .replace("]\"\"", "]")
      .replaceAll("\\s+", " ")
      .replaceAll("(\"\"\")|( \")|(\" )", "\"")
      .replace(": ", ":")
    bodyRequest
  }
  def url: String = uriEndpoint.toString
  def method: String = post

}
