package com.vitornp.choice.lunch.route

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.pattern.ask
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.vitornp.choice.lunch.actor.ChoiceActor.Choice
import com.vitornp.choice.lunch.model.{Lunch, Weight}

import scala.concurrent.Future

object ChoiceRoute extends Directives with TimeoutSupport {

  implicit val desId: Unmarshaller[String, Weight.Weight] = Unmarshaller.strict[String, Weight.Weight] { s =>
    Weight.withName(s)
  }

  def apply(system: ActorSystem, choiceActor: ActorRef, corsSettings: CorsSettings.Default): Route = {
    import de.heikoseeberger.akkahttpjackson.JacksonSupport._

    logRequestResult("api-choices") {
      cors(corsSettings) {
        pathPrefix("api" / "choices") {
          pathEndOrSingleSlash {
            get {
              parameters('time.as[Weight.Weight], 'price.as[Weight.Weight]) { (time, price) =>
                system.log.info("Choice lunch by time [{}] and price [{}]", time, price)
                val lunch: Future[Option[Lunch]] = (choiceActor ? Choice(time, price)).mapTo[Option[Lunch]]
                onSuccess(lunch) {
                  case Some(result) =>
                    complete((StatusCodes.OK, result))
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
}
