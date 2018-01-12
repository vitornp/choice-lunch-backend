package com.vitornp.choice.lunch

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.vitornp.choice.lunch.actor.LunchActor
import com.vitornp.choice.lunch.route.LunchRoute

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object QuickstartServer extends App with LunchRoute {

  // set up ActorSystem and other dependencies here
  implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // Needed for the Future and its methods flatMap/onComplete in the end
  implicit val executionContext: ExecutionContext = system.dispatcher

  val lunchActor: ActorRef = system.actorOf(LunchActor.props, "lunchActor")

  lazy val routes: Route = lunchRoutes

  val serverBindingFuture: Future[ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

  StdIn.readLine()

  serverBindingFuture
    .flatMap(_.unbind())
    .onComplete { done =>
      done.failed.map { ex => log.error(ex, "Failed unbinding") }
      system.terminate()
    }

}
