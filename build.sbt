enablePlugins(JavaAppPackaging)

organization := "com.vitornp"
name := "choice-lunch-backend"
version := "1.0"
scalaVersion := "2.12.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

lazy val akkaVersion = "2.5.9"
lazy val akkaHttpVersion = "10.0.11"
lazy val akkaHttpJacksonVersion = "1.19.0"
lazy val akkaPersistenceMongoVersion = "2.0.4"
lazy val casbahVersion = "3.1.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,

  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.github.scullxbones" %% "akka-persistence-mongo-casbah" % akkaPersistenceMongoVersion,
  "org.mongodb" %% "casbah" % casbahVersion,

  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "de.heikoseeberger" %% "akka-http-jackson" % akkaHttpJacksonVersion,

  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)

Revolver.settings
