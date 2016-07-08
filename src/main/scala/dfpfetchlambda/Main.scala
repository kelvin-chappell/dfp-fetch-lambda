package dfpfetchlambda

import dfpfetchlambda.ReportQueries.{allHostedCampaigns, renaultMerchComponents}

class Main {
  def handler(): Unit = {

    S3.write(
      report = Dfp.fetchReport(allHostedCampaigns),
      dstFile = "LabsHostedCampaigns.csv"
    )

    S3.write(
      report = Dfp.fetchReport(renaultMerchComponents),
      dstFile = "LabsRenaultMerch.csv"
    )
  }
}
