package dfpfetchlambda

object Config {

  private lazy val props = S3.readConfig()

  private def getRequired(key: String): String = {
    props.getOrElse(key, {
      throw new IllegalArgumentException(s"Property '$key' not configured")
    })
  }

  object dfp {
    val clientId = getRequired("dfp.clientId")
    val clientSecret = getRequired("dfp.clientSecret")
    val refreshToken = getRequired("dfp.refreshToken")
    val appName = "Hosted report fetcher"
    val networkCode = "59666047"
  }

  object s3 {
    val configBucketName = "kelvin-test"
    val configKey = "config.properties"
    val reportBucketName = "kelvin-test"
    val reportKey = "dfp-report"
  }
}
