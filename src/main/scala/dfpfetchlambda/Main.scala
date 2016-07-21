package dfpfetchlambda

import dfpfetchlambda.ReportQueries.{leffeMerchComponents, leffeNativeCards}

class Main {
  def handler(): Unit = {
    Dfp.inSession { session =>

      S3.write(
        report = Dfp.fetchReport(leffeMerchComponents)(session),
        dstFile = "LabsHostedLeffeMerch.csv"
      )

      S3.write(
        report = Dfp.fetchReport(leffeNativeCards)(session),
        dstFile = "LabsHostedLeffeCards.csv"
      )
    }
  }
}
