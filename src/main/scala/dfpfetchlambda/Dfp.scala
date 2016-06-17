package dfpfetchlambda

import com.google.api.ads.common.lib.auth.OfflineCredentials
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api.DFP
import com.google.api.ads.dfp.axis.factory.DfpServices
import com.google.api.ads.dfp.axis.utils.v201605.{ReportDownloader, StatementBuilder}
import com.google.api.ads.dfp.axis.v201605.DateRangeType.REACH_LIFETIME
import com.google.api.ads.dfp.axis.v201605.ExportFormat.CSV_DUMP
import com.google.api.ads.dfp.axis.v201605._
import com.google.api.ads.dfp.lib.client.DfpSession
import dfpfetchlambda.Config.dfp

import scala.io.Source

object Dfp {

  def fetchReport(): String = {

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

    val reportQuery = {
      val query = new ReportQuery()
      query.setDateRangeType(REACH_LIFETIME)
      val lineItems = Seq(
        119417007,
        119508327,
        121546407,
        121572687,
        121592247,
        123009207,
        123009327,
        123009447
      ) mkString ","
      query.setStatement(
        new StatementBuilder()
          .where(s"LINE_ITEM_ID IN ($lineItems)")
          .toStatement
      )
      query.setDimensions(Array(Dimension.DATE))
      query.setColumns(Array(
        Column.TOTAL_INVENTORY_LEVEL_IMPRESSIONS,
        Column.TOTAL_INVENTORY_LEVEL_CLICKS,
        Column.TOTAL_INVENTORY_LEVEL_CTR
      ))
      query
    }

    val reportService = new DfpServices().get(session, classOf[ReportServiceInterface])

    val reportJob = {
      val job = new ReportJob()
      job.setReportQuery(reportQuery)
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
