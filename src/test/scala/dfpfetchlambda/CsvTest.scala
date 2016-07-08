package dfpfetchlambda

import org.specs2.mutable.Specification

class CsvTest extends Specification {

  private val report =
    """
       header
    2016-06-29,123894208,oA,cA,fA,9971,2,0.0020
    2016-06-29,123894328,oA,cA,fA,196661,70,0.0036
    2016-06-29,123894209,oA,cA,fB,99712,2,0.0020
    2016-06-29,123894329,oA,cA,fB,196661,70,0.0036
    2016-06-29,123894207,oA,cB,fA,997,2,0.0020
    2016-06-29,123894327,oA,cB,fA,19666,70,0.0036
    2016-06-29,123894447,oB,cC,fA,2310,8,0.0035
    2016-07-04,123893487,oA,cA,fA,2,0,0.0000
    2016-07-04,123893727,oA,cA,fA,6346,18,0.0028
    2016-07-04,124416927,oB,cB,fA,6,0,0.0000
    """

  "groupByCampaignAndFormat" >> {
    Csv.groupByCampaignAndFormat(report) must_== Seq(
      DataPoint(
        DataKey(date = "2016-06-29", owner = "oA", campaign = "cA", format = "fA"),
        DataValue(impressionCount = 206632, clickCount = 72, ctr = 0.00034844554570444074)
      ),
      DataPoint(
        DataKey(date = "2016-06-29", owner = "oA", campaign = "cA", format = "fB"),
        DataValue(impressionCount = 296373, clickCount = 72, 0.00024293710965573787)
      ),
      DataPoint(
        DataKey(date = "2016-06-29", owner = "oA", campaign = "cB", format = "fA"),
        DataValue(impressionCount = 20663, clickCount = 72, ctr = 0.003484489183564826)
      ),
      DataPoint(
        DataKey(date = "2016-06-29", owner = "oB", campaign = "cC", format = "fA"),
        DataValue(impressionCount = 2310, clickCount = 8, 0.003463203463203463)
      ),
      DataPoint(
        DataKey(date = "2016-07-04", owner = "oA", campaign = "cA", format = "fA"),
        DataValue(impressionCount = 6348, clickCount = 18, 0.002835538752362949)
      ),
      DataPoint(
        DataKey(date = "2016-07-04", owner = "oB", campaign = "cB", format = "fA"),
        DataValue(impressionCount = 6, 0, 0.0)
      )
    )
  }
}
