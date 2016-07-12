package dfpfetchlambda

import dfpfetchlambda.ReportQueries.leffeMerchComponents

class Main {
  def handler(): Unit = {
    S3.write(
      report = Dfp.fetchReport(leffeMerchComponents),
      dstFile = "LabsHostedLeffeMerch.csv"
    )
  }
}
