package com.vitornp.choice.lunch

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.vitornp.choice.lunch.actor.LunchActor
import com.vitornp.choice.lunch.route.LunchRoute

import scala.concurrent.ExecutionContext

object QuickstartServer extends App with LunchRoute {

  // set up ActorSystem and other dependencies here
  implicit val system: ActorSystem = ActorSystem("choiceLunchBackend")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // Needed for the Future and its methods flatMap/onComplete in the end
  implicit val executionContext: ExecutionContext = system.dispatcher

  val lunchActor: ActorRef = system.actorOf(LunchActor.props, "lunchActor")

  lazy val routes: Route = lunchRoutes

  val config = ConfigFactory.load()

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
