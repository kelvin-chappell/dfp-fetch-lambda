package dfpfetchlambda

import org.specs2.mutable.Specification

class CsvTest extends Specification {

  private val report =
    """
      |header
      |2016-06-29,123894208,oA,cA,fA,9971,2,0.0020
      |2016-06-29,123894328,oA,cA,fA,196661,70,0.0036
      |2016-06-29,123894209,oA,cA,fB,99712,2,0.0020
      |2016-06-29,123894329,oA,cA,fB,196661,70,0.0036
      |2016-06-29,123894207,oA,cB,fA,997,2,0.0020
      |2016-06-29,123894327,oA,cB,fA,19666,70,0.0036
      |2016-06-29,123894447,oB,cC,fA,2310,8,0.0035
      |2016-07-04,123893487,oA,cA,fA,2,0,0.0000
      |2016-07-04,123893727,oA,cA,fA,6346,18,0.0028
      |2016-07-04,124416927,oB,cC,fA,6,0,0.0000
      |2016-07-05,124416927,oB,cC,fA,0,0,0.0000
    """.stripMargin

  "splitByCampaignAndFormat" >> {
    Csv.splitByCampaignAndFormat(report) must_== Seq(
      DataSet(
        name = "oA-cA-fA.csv",
        dataPointCsv =
          """
            |2016-06-29,206632,72,0.0003
            |2016-07-04,6348,18,0.0028
          """.stripMargin.trim
      ),
      DataSet(
        name = "oA-cA-fB.csv",
        dataPointCsv =
          """
            |2016-06-29,296373,72,0.0002
          """.stripMargin.trim
      ),
      DataSet(
        name = "oA-cB-fA.csv",
        dataPointCsv =
          """
            |2016-06-29,20663,72,0.0035
          """.stripMargin.trim
      ),
      DataSet(
        name = "oB-cC-fA.csv",
        dataPointCsv =
          """
            |2016-06-29,2310,8,0.0035
            |2016-07-04,6,0,0.0000
            |2016-07-05,0,0,0.0000
          """.stripMargin.trim
      )
    )
  }
}
