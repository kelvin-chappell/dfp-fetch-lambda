package dfpfetchlambda

import dfpfetchlambda.ReportQueries.{leffeMerchComponents, leffeNativeCards}

class Main {
  def handler(): Unit = {

    S3.write(
      report = Dfp.fetchReport(leffeMerchComponents),
      dstFile = "LabsHostedLeffeMerch.csv"
    )

    S3.write(
      report = Dfp.fetchReport(leffeNativeCards),
      dstFile = "LabsHostedLeffeCards.csv"
    )
  }
}
