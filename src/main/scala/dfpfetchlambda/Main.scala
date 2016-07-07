package dfpfetchlambda

import dfpfetchlambda.ReportQueries.hostedCampaigns

class Main {
  def handler(): Unit = {
    val report = Dfp.fetchReport(hostedCampaigns)
    S3.write(report)
  }
}
