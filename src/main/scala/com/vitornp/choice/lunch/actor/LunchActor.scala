package com.vitornp.choice.lunch.actor

import akka.actor.{Actor, ActorLogging, Props}
import com.vitornp.choice.lunch.model.{Lunch, Lunchs}

object LunchActor {

  final case class ActionPerformed(description: String)

  final case object GetLunchs

  final case class CreateLunch(lunch: Lunch)

  final case class DeleteLunch(name: String)

  def props: Props = Props[LunchActor]

}

class LunchActor extends Actor with ActorLogging {

  import LunchActor._

  var lunchs = Set.empty[Lunch]

  override def receive: Receive = {
    case GetLunchs =>
      sender() ! Lunchs(lunchs.toSeq)
    case CreateLunch(lunch) =>
      lunchs += lunch
      sender() ! ActionPerformed(s"Lunch ${lunch.name} created.")
    case DeleteLunch(name) =>
      lunchs.find(_.name == name) foreach { lunch => lunchs -= lunch }
      sender() ! ActionPerformed(s"Lunch $name deleted.")
  }

}
