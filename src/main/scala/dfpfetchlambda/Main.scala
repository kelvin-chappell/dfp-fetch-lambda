package dfpfetchlambda

import dfpfetchlambda.ReportQueries.renaultMerchComponents

class Main {
  def handler(): Unit = {
    S3.write(
      report = Dfp.fetchReport(renaultMerchComponents),
      dstFile = "LabsHostedRenaultMerch.csv"
    )
  }
}
