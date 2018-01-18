package com.vitornp.choice.lunch

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.vitornp.choice.lunch.actor.LunchActor
import com.vitornp.choice.lunch.route.{Healthcheck, LunchRoute}

import scala.concurrent.ExecutionContext

object ChoiceLunchServer extends App with Directives {

  implicit val system: ActorSystem = ActorSystem("choiceLunchBackend")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val lunchActor: ActorRef = system.actorOf(LunchActor.props, "lunchActor")

  val routes: Route =
    Healthcheck() ~
      LunchRoute(system, lunchActor)

  val config = ConfigFactory.load()

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
