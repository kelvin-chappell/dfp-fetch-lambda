package dfpfetchlambda

class Main {
  def handler(): Unit = {
    val report = Dfp.fetchReport()
    S3.write(report)
  }
}
