package com.vitornp.choice.lunch.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.vitornp.choice.lunch.model.{Lunch, Lunches}

object LunchActor {

  final case class ActionPerformed(description: String)

  final case object GetLunches

  final case class CreateLunch(lunch: Lunch)

  final case class DeleteLunch(name: String)

  def props: Props = Props[LunchActor]

}

class LunchActor extends Actor with ActorLogging {

  import LunchActor._

  var lunches = Set.empty[Lunch]

  override def receive: Receive = {
    case GetLunches =>
      sender() ! Lunches(lunches.toSeq)
    case CreateLunch(lunch) =>
      lunches += lunch
      sender() ! ActionPerformed(s"Lunch ${lunch.name} created.")
    case DeleteLunch(name) =>
      lunches.find(_.name == name) foreach { lunch => lunches -= lunch }
      sender() ! ActionPerformed(s"Lunch $name deleted.")
  }

}
