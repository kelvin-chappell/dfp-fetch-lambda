package dfpfetchlambda

import com.google.api.ads.common.lib.auth.OfflineCredentials
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api.DFP
import com.google.api.ads.dfp.axis.factory.DfpServices
import com.google.api.ads.dfp.axis.utils.v201605.{ReportDownloader, StatementBuilder}
import com.google.api.ads.dfp.axis.v201605.Column.{TOTAL_INVENTORY_LEVEL_CLICKS, TOTAL_INVENTORY_LEVEL_CTR, TOTAL_INVENTORY_LEVEL_IMPRESSIONS}
import com.google.api.ads.dfp.axis.v201605.DateRangeType.REACH_LIFETIME
import com.google.api.ads.dfp.axis.v201605.Dimension.{DATE, LINE_ITEM_ID}
import com.google.api.ads.dfp.axis.v201605.ExportFormat.CSV_DUMP
import com.google.api.ads.dfp.axis.v201605._
import com.google.api.ads.dfp.lib.client.DfpSession
import dfpfetchlambda.Config.dfp

import scala.io.Source

object Dfp {

  def inSession[T](action: DfpSession => T): T = {

    val credentials = new OfflineCredentials.Builder()
                      .forApi(DFP)
                      .withClientSecrets(dfp.clientId, dfp.clientSecret)
                      .withRefreshToken(dfp.refreshToken)
                      .build()
                      .generateCredential()

    val session = new DfpSession.Builder()
                  .withOAuth2Credential(credentials)
                  .withApplicationName(dfp.appName)
                  .withNetworkCode(dfp.networkCode)
                  .build()

    action(session)
  }

  def fetchReport(qry: ReportQuery)(session: DfpSession): String = {

    val reportService = new DfpServices().get(session, classOf[ReportServiceInterface])

    val reportJob = {
      val job = new ReportJob()
      job.setReportQuery(qry)
      reportService.runReportJob(job)
    }

    val reportDownloader = new ReportDownloader(reportService, reportJob.getId)
    reportDownloader.waitForReportReady()

    val source = {
      val options = new ReportDownloadOptions()
      options.setExportFormat(CSV_DUMP)
      options.setUseGzipCompression(false)
      reportDownloader.getDownloadUrl(options)
    }

    Source.fromURL(source).mkString
  }
}

object ReportQueries {

  val allHostedCampaigns: ReportQuery = {
    val hostedOrderId = 345535767
    val sponsorCustomFieldId = 1527

    val query = new ReportQuery()
    query.setDateRangeType(REACH_LIFETIME)
    query.setStatement(
      new StatementBuilder()
      .where("ORDER_ID = :hostedOrderId")
      .withBindVariableValue("hostedOrderId", hostedOrderId)
      .toStatement
    )
    query.setDimensions(Array(DATE, LINE_ITEM_ID))
    query.setCustomFieldIds(Array(sponsorCustomFieldId))
    query.setColumns(
      Array(
        TOTAL_INVENTORY_LEVEL_IMPRESSIONS,
        TOTAL_INVENTORY_LEVEL_CLICKS,
        TOTAL_INVENTORY_LEVEL_CTR
      )
    )

    query
  }

  private def hostedLineItems(lineItemIds: Long*): ReportQuery = {
    val query = new ReportQuery()
    query.setDateRangeType(REACH_LIFETIME)
    query.setStatement(
      new StatementBuilder()
      .where(s"LINE_ITEM_ID IN (${lineItemIds.mkString(",")})")
      .toStatement
    )
    query.setDimensions(Array(DATE))
    query.setColumns(
      Array(
        TOTAL_INVENTORY_LEVEL_IMPRESSIONS,
        TOTAL_INVENTORY_LEVEL_CLICKS,
        TOTAL_INVENTORY_LEVEL_CTR
      )
    )
    query
  }

  val renaultMerchComponents = hostedLineItems(
    119417007,
    119508327,
    121546407,
    121572687,
    121592247,
    123009207,
    123009327,
    123009447
  )

  val leffeMerchComponents = hostedLineItems(124416927)
  val leffeNativeCards = hostedLineItems(125762007)
}

object TestReport extends App {

  def report(rptName: String, qry: ReportQuery) = {
    println(rptName)
    val rpt = Dfp.inSession(Dfp.fetchReport(qry))
    println(rpt)
  }

  println("*** Start ***")

  report("*** all ***", ReportQueries.allHostedCampaigns)
  report("*** renault merch ***", ReportQueries.renaultMerchComponents)

  println("*** End ***")
}
