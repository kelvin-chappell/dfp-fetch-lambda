lazy val root = (project in file(".")).
  settings(
    name := "dfp-fetch-lambda",
    version := "1.0",
    scalaVersion := "2.11.8",
    retrieveManaged := true,
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
      "com.amazonaws" % "aws-lambda-java-events" % "1.3.0",
      "com.google.api-ads" % "dfp-axis" % "2.16.0",
      "javax.mail" % "javax.mail-api" % "1.5.5",
      "org.slf4j" % "slf4j-nop" % "1.7.21"
    )
  )

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
