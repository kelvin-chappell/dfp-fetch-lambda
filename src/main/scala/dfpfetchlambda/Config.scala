package dfpfetchlambda

import java.io.{FileInputStream, InputStream}
import java.util.Properties

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

object Config {

  private lazy val props = {

    def readConfig(propStream: InputStream): Map[String, String] = {
      val props = new Properties()
      try {
        props.load(propStream)
        props.asScala.toMap
      } finally {
        try {
          propStream.close()
        } catch {
          case NonFatal(_) => /*ignore*/
        }
      }
    }

    if (sys.props.get("dev").nonEmpty) {
      val standardDfpPropertiesFile = s"${sys.props.get("user.home").get}/ads.properties"
      val props = readConfig(new FileInputStream(standardDfpPropertiesFile))
      props map { case (k, v) => k.stripPrefix("api.dfp.") -> v }
    } else {
      readConfig(S3.openS3PropertiesStream())
    }
  }

  private def getRequired(key: String): String = {
    props.getOrElse(key, {
      throw new IllegalArgumentException(s"Property '$key' not configured")
    })
  }

  object dfp {
    val clientId = getRequired("clientId")
    val clientSecret = getRequired("clientSecret")
    val refreshToken = getRequired("refreshToken")
    val appName = "Hosted report fetcher"
    val networkCode = "59666047"
  }

  object s3 {
    val configBucketName = "dfp-credentials"
    val configKey = "api.properties"
    val reportBucketName = "omniture-dash-data"
    val reportSubfolder = "dfp"
  }
}
