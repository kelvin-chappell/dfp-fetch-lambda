package dfpfetchlambda

object Csv {

  def fileFromDataPoints(dataPoints: Seq[DataPoint]): String = {
    dataPoints.map(rowFromDataPoint) mkString "\n"
  }

  def rowFromDataPoint(dataPoint: DataPoint): String = {
    val key = dataPoint.key
    val value = dataPoint.value
    val ctr = f"${value.ctr}%1.4f"
    s"${key.date},${value.impressionCount},${value.clickCount},$ctr"
  }

  // todo: split first and then aggregate by date
  def splitByCampaignAndFormat(report: String): Seq[DataSet] =
    DataSet.splitByCampaignAndFormat(DataPoint.groupByDateCampaignAndFormat(report))
}

case class DataSet(
  name: String,
  dataPointCsv: String
)

object DataSet {

  def splitByCampaignAndFormat(dataPoints: Seq[DataPoint]): Seq[DataSet] = {
    dataPoints.groupBy { dataPoint =>
      (dataPoint.key.owner, dataPoint.key.campaign, dataPoint.key.format)
    }.map { case ((owner, campaign, format), campaignFormatDataPoints) =>
      DataSet(
        name = s"$owner-$campaign-$format.csv",
        dataPointCsv = Csv.fileFromDataPoints(campaignFormatDataPoints)
      )
    }.toSeq.sortBy(_.name)
  }
}

case class DataKey(
  date: String,
  owner: String,
  campaign: String,
  format: String
)

case class DataValue(
  impressionCount: Int,
  clickCount: Int,
  ctr: Double
)

case class DataPoint(
  key: DataKey,
  value: DataValue
)

object DataPoint {

  def fromCsvFile(content: String): Seq[DataPoint] =
    content.trim.lines.toSeq.tail.map(fromCsvRow)

  def fromCsvRow(row: String): DataPoint = {
    val parts = row.split(",")
    DataPoint(
      DataKey(
        parts(0),
        parts(2),
        parts(3),
        parts(4)
      ),
      DataValue(
        parts(5).toInt,
        parts(6).toInt,
        parts(7).toDouble
      )
    )
  }

  def groupByDateCampaignAndFormat(report: String): Seq[DataPoint] = {

    def aggregate(values: Seq[DataValue]): DataValue = {
      val impressionCount = values.map(_.impressionCount).sum
      val clickCount = values.map(_.clickCount).sum
      DataValue(
        impressionCount,
        clickCount,
        if (impressionCount == 0) 0 else clickCount.toDouble / impressionCount
      )
    }

    fromCsvFile(report).groupBy(_.key).map { case (key, dataPoints) =>
      DataPoint(key, aggregate(dataPoints.map(_.value)))
    }.toSeq.sortBy { case DataPoint(key, _) =>
      (key.date, key.owner, key.campaign, key.format)
    }
  }
}
