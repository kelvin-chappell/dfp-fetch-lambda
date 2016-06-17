package dfpfetchlambda

import java.util.Properties

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{GetObjectRequest, ObjectMetadata}
import com.amazonaws.util.StringInputStream
import dfpfetchlambda.Config.s3

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

object S3 {

  private lazy val client = new AmazonS3Client()

  def readConfig(): Map[String, String] = {
    val s3Properties = client.getObject(new GetObjectRequest(s3.configBucketName, s3.configKey)).getObjectContent
    val props = new Properties()
    try {
      props.load(s3Properties)
      props.asScala.toMap
    } finally {
      try {
        s3Properties.close()
      } catch {
        case NonFatal(_) => /*ignore*/
      }
    }
  }

  def write(report: String): Unit = {
    val inputStream = new StringInputStream(report)

    val metadata = new ObjectMetadata()
    metadata.setContentType("text/csv; charset=utf-8")
    metadata.setContentLength(report.getBytes.length)

    client.putObject(s3.reportBucketName, s3.reportKey, inputStream, metadata)
  }
}
