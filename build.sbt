name := "AgregadoresyAprobadores"

version := "0.1"

scalaVersion := "2.13.6"

idePackagePrefix := Some("eci.edu.co")

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test
libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.8"
libraryDependencies += "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.39.8" % "test"
libraryDependencies += "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.39.8" % "test"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.23"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.0.0"
libraryDependencies += "org.apache.kafka" %% "kafka-streams-scala" % "3.0.0"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.2"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.10.62"

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

// (optional) If you need scalapb/scalapb.proto or anything from
// google/protobuf/*.proto
libraryDependencies ++= Seq(
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
)

Test / fork := true