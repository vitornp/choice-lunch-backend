package com.vitornp.choice.lunch.route

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import com.vitornp.choice.lunch.actor.LunchActor._
import com.vitornp.choice.lunch.model.{Lunch, Lunches}

import scala.concurrent.Future

object LunchRoute extends Directives with TimeoutSupport {

  def apply(system: ActorSystem, lunchActor: ActorRef): Route = {

    import de.heikoseeberger.akkahttpjackson.JacksonSupport._

    logRequestResult("api-lunches") {
      pathPrefix("api" / "lunches") {
        pathEndOrSingleSlash {
          get {
            val lunches: Future[Option[Lunches]] = (lunchActor ? GetAll).mapTo[Option[Lunches]]
            onSuccess(lunches) {
              case Some(result) =>
                complete((StatusCodes.OK, result))
              case None =>
                complete(StatusCodes.NoContent)
            }
          } ~
            post {
              entity(as[Lunch]) { lunch =>
                val lunchCreated: Future[Option[Lunch]] = (lunchActor ? Create(lunch)).mapTo[Option[Lunch]]
                onSuccess(lunchCreated) {
                  case Some(result) =>
                    system.log.info("Created lunch [{}]", result.id)
                    complete((StatusCodes.Created, result))
                  case None =>
                    complete(StatusCodes.Conflict)
                }
              }
            }
        } ~
          path(JavaUUID) { id =>
            pathEndOrSingleSlash {
              get {
                val lunch: Future[Option[Lunch]] = (lunchActor ? GetById(id)).mapTo[Option[Lunch]]
                onSuccess(lunch) {
                  case Some(result) =>
                    complete((StatusCodes.OK, result))
                  case None =>
                    complete(StatusCodes.NotFound)
                }
              } ~
                put {
                  entity(as[Lunch]) { lunch =>
                    val lunchUpdated: Future[Option[Lunch]] = (lunchActor ? Update(id, lunch)).mapTo[Option[Lunch]]
                    onSuccess(lunchUpdated) {
                      case Some(result) =>
                        system.log.info("Updated lunch [{}]", id)
                        complete((StatusCodes.OK, result))
                      case None =>
                        complete(StatusCodes.NotFound)
                    }
                  }
                } ~
                delete {
                  val lunchDeleted: Future[Option[DeleteById]] = (lunchActor ? DeleteById(id)).mapTo[Option[DeleteById]]
                  onSuccess(lunchDeleted) {
                    case Some(_) =>
                      system.log.info("Removed lunch [{}]", id)
                      complete(StatusCodes.OK, s"Lunch $id deleted")
                    case None =>
                      complete(StatusCodes.NotFound)
                  }
                }
            }
          }
      }
    }
  }

}
