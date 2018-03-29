enablePlugins(JavaAppPackaging)

resolvers += Resolver.sbtPluginRepo("releases")

organization := "com.vitornp"
name := "choice-lunch-backend"
version := "1.0"
scalaVersion := "2.12.5"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

lazy val akkaVersion = "2.5.11"
lazy val akkaPersistenceMongoVersion = "2.0.7"
lazy val casbahVersion = "3.1.1"
lazy val akkaHttpVersion = "10.1.0"
lazy val akkaHttpJacksonVersion = "1.20.0"
lazy val akkaHttpCorsVersion = "0.2.2"
lazy val scalatestVersion = "3.0.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,

  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.github.scullxbones" %% "akka-persistence-mongo-casbah" % akkaPersistenceMongoVersion,
  "org.mongodb" %% "casbah" % casbahVersion,

  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "de.heikoseeberger" %% "akka-http-jackson" % akkaHttpJacksonVersion,
  "ch.megard" %% "akka-http-cors" % akkaHttpCorsVersion,

  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "org.scalatest" %% "scalatest" % scalatestVersion % Test
)

Revolver.settings
