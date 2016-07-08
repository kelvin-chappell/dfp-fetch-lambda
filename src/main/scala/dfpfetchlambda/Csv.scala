package dfpfetchlambda

object Csv {

  def groupByCampaignAndFormat(report: String): Seq[DataPoint] = {

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
        clickCount.toDouble / impressionCount
      )
    }

    report.trim.lines.toSeq.tail.map(makeRow).groupBy(_.key).map { case (key, dataPoints) =>
      DataPoint(key, aggregate(dataPoints.map(_.value)))
    }.toSeq.sortBy { case DataPoint(key, _) =>
      (key.date, key.owner, key.campaign, key.format)
    }
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
