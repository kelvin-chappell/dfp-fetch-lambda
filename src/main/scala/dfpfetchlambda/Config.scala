package dfpfetchlambda

object Config {

  private lazy val props = S3.readConfig()

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
    val configBucketName = "omniture-dash-data"
    val configKey = "DfpApi.properties"
    val reportBucketName = "omniture-dash-data"
    val reportKey = "DfpCommercialLabsHostedCampaigns.csv"
  }
}
