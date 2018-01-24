package com.vitornp.choice.lunch

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.{HttpOrigin, HttpOriginRange}
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.typesafe.config.ConfigFactory
import com.vitornp.choice.lunch.actor.{ChoiceActor, LunchActor}
import com.vitornp.choice.lunch.route.{ChoiceRoute, Healthcheck, LunchRoute}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext

object ChoiceLunchServer extends App with Directives {

  implicit val system: ActorSystem = ActorSystem("choiceLunchBackend")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val configHttp = ConfigFactory.load().getConfig("http")

  val corsOriginList: List[HttpOrigin] = configHttp
    .getStringList("corsOrigin")
    .asScala
    .iterator
    .toList
    .map(origin => HttpOrigin(origin))

  val corsSettings: CorsSettings.Default = CorsSettings.defaultSettings
    .copy(allowedOrigins = HttpOriginRange(corsOriginList: _*))

  val lunchActor: ActorRef = system.actorOf(LunchActor.props, "lunchActor")
  val choiceActor: ActorRef = system.actorOf(ChoiceActor.props(lunchActor), "choiceActor")

  val routes: Route =
    Healthcheck() ~
      LunchRoute(system, lunchActor, corsSettings) ~
      ChoiceRoute(system, choiceActor, corsSettings)

  Http().bindAndHandle(routes, configHttp.getString("interface"), configHttp.getInt("port"))
}
