package utilities

import io.github.cdimascio.dotenv.Dotenv

class EnvironmentValues() {

  var dotenv: Dotenv = null

  if (System.getenv("GITHUB_ACTIONS") == null) {
    dotenv = Dotenv.configure.load
    System.out.println("Variables de entorno cargadas")
  }

  def getenv(key: String): String = if (System.getenv("GITHUB_ACTIONS") == null) dotenv.get(key)
  else System.getenv(key)
}
