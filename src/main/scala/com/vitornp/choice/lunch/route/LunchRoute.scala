package com.vitornp.choice.lunch.route

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.vitornp.choice.lunch.actor.LunchActor.{ActionPerformed, CreateLunch, DeleteLunch, GetLunchs}
import com.vitornp.choice.lunch.model.Weight.Weight
import com.vitornp.choice.lunch.model.{Lunch, Lunchs, Weight}
import com.vitornp.choice.lunch.util.EnumJsonConverter
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.Future
import scala.concurrent.duration._

trait LunchRoute extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val weightJsonFormat: RootJsonFormat[Weight] = new EnumJsonConverter(Weight)
  implicit val lunchJsonFormat: RootJsonFormat[Lunch] = jsonFormat3(Lunch)
  implicit val lunchsJsonFormat: RootJsonFormat[Lunchs] = jsonFormat1(Lunchs)
  implicit val actionPerformedJsonFormat: RootJsonFormat[ActionPerformed] = jsonFormat1(ActionPerformed)

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[LunchRoute])

  def lunchActor: ActorRef

  implicit lazy val timeout: Timeout = Timeout(5 seconds) // usually we'd obtain the timeout from the system's configuration

  val lunchRoutes: Route = pathPrefix("lunchs") {
    concat(
      pathEnd {
        concat(
          get {
            val lunchs: Future[Lunchs] = (lunchActor ? GetLunchs).mapTo[Lunchs]
            complete(lunchs)
          },
          post {
            entity(as[Lunch]) { lunch =>
              val lunchCreated: Future[ActionPerformed] = (lunchActor ? CreateLunch(lunch)).mapTo[ActionPerformed]
              onSuccess(lunchCreated) { performed =>
                log.info("Created lunch [{}]: {}", lunch.name, performed.description)
                complete((StatusCodes.Created, performed))
              }
            }
          }
        )
      },
      path(Segment) { name =>
        delete {
          val lunchDeleted: Future[ActionPerformed] = (lunchActor ? DeleteLunch(name)).mapTo[ActionPerformed]
          onSuccess(lunchDeleted) { performed =>
            log.info("Deleted lunch [{}]: {}", name, performed.description)
            complete((StatusCodes.OK, performed))
          }
        }
      }
    )
  }

}
