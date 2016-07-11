package dfpfetchlambda

object Csv {

  def splitByCampaignAndFormat(report: String): Seq[DataSet] =
    splitByCampaignAndFormat(groupByDateCampaignAndFormat(report))

  private def groupByDateCampaignAndFormat(report: String): Seq[DataPoint] = {

    def makeRow(row: String): DataPoint = {
      val parts = row.split(",")
      def trimmed(i: Int): String = parts(i).trim
      DataPoint(
        DataKey(
          trimmed(0),
          trimmed(2),
          trimmed(3),
          trimmed(4)
        ),
        DataValue(
          trimmed(5).toInt,
          trimmed(6).toInt,
          trimmed(7).toDouble
        )
      )
    }

    def aggregate(values: Seq[DataValue]): DataValue = {
      val impressionCount = values.map(_.impressionCount).sum
      val clickCount = values.map(_.clickCount).sum
      DataValue(
        impressionCount,
        clickCount,
        if (impressionCount == 0) 0 else clickCount.toDouble / impressionCount
      )
    }

    report.trim.lines.toSeq.tail.map(makeRow).groupBy(_.key).map { case (key, dataPoints) =>
      DataPoint(key, aggregate(dataPoints.map(_.value)))
    }.toSeq.sortBy { case DataPoint(key, _) =>
      (key.date, key.owner, key.campaign, key.format)
    }
  }

  private def splitByCampaignAndFormat(dataPoints: Seq[DataPoint]): Seq[DataSet] = {
    dataPoints.groupBy { dataPoint =>
      (dataPoint.key.owner, dataPoint.key.campaign, dataPoint.key.format)
    }.map { case ((owner, campaign, format), campaignFormatDataPoints) =>
      val dataPointCsv = campaignFormatDataPoints map { case DataPoint(key, value) =>
        val ctr = f"${value.ctr}%1.4f"
        s"${key.date},${value.impressionCount},${value.clickCount},$ctr"
      } mkString "\n"
      DataSet(name = s"$owner-$campaign-$format.csv", dataPointCsv)
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

case class DataSet(
  name: String,
  dataPointCsv: String
)
