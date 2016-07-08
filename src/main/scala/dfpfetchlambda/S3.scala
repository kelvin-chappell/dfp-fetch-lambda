package dfpfetchlambda

import java.io.InputStream

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{GetObjectRequest, ObjectMetadata}
import com.amazonaws.util.StringInputStream
import dfpfetchlambda.Config.s3

object S3 {

  private lazy val client = new AmazonS3Client()

  def openS3PropertiesStream(): InputStream =
    client.getObject(new GetObjectRequest(s3.configBucketName, s3.configKey)).getObjectContent

  def write(report: String, dstFile: String): Unit = {
    val inputStream = new StringInputStream(report)

    val metadata = new ObjectMetadata()
    metadata.setContentType("text/csv; charset=utf-8")
    metadata.setContentLength(report.getBytes.length)

    val key = s"${s3.reportSubfolder}/$dstFile"
    client.putObject(s3.reportBucketName, key, inputStream, metadata)
  }
}
