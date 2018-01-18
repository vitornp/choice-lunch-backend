package com.vitornp.choice.lunch.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.{ask, pipe}
import com.vitornp.choice.lunch.model.Lunches
import com.vitornp.choice.lunch.model.Weight.Weight

import scala.util.Random

object ChoiceActor {

  def props(lunchActor: ActorRef) = Props(new ChoiceActor(lunchActor))

  final case class Choice(time: Weight, price: Weight)

}

class ChoiceActor(lunchActor: ActorRef) extends Actor with ActorLogging {

  import ChoiceActor._
  import LunchActor._
  import akka.util.Timeout
  import context.dispatcher

  import scala.concurrent.duration._

  implicit val timeout: Timeout = Timeout(25 seconds)

  override def receive: Receive = {
    case choice: Choice =>
      (lunchActor ? GetAllByTimeAndPrice(choice.time, choice.price))
        .mapTo[Option[Lunches]]
        .map { results =>
          if (results.isDefined) {
            val lunches = results.getOrElse[Lunches](new Lunches()).lunches
            Some(Random.shuffle(lunches).head)
          } else {
            None
          }

        } pipeTo sender()
  }

}
