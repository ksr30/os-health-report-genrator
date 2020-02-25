name := "os-health-report-genrator"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.12",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.12" % Test
)

libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % "test"
