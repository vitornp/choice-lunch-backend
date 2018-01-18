package com.vitornp.choice.lunch.actor

import java.util.UUID

import akka.actor.{ActorLogging, Props}
import akka.persistence.{PersistentActor, SnapshotOffer}
import com.vitornp.choice.lunch.model.{Lunch, Lunches}

object LunchActor {
  def props: Props = Props[LunchActor]

  final case class Create(lunch: Lunch)

  final case class GetById(id: UUID)

  final case object GetAll

  final case class Update(id: UUID, lunch: Lunch)

  final case class DeleteById(id: UUID)

  final case class LunchState(lunches: Map[UUID, Lunch]) {
    def apply(id: UUID): Option[Lunch] = lunches.get(id)

    def +(id: UUID, lunch: Lunch): LunchState = LunchState(lunches.updated(id, lunch))

    def -(id: UUID): LunchState = LunchState(lunches.filterKeys(_ != id))

    def contains(id: UUID): Boolean = lunches.contains(id)
  }

  object LunchState {
    def apply(): LunchState = LunchState(Map.empty)
  }

}

class LunchActor extends PersistentActor with ActorLogging {

  import LunchActor._

  var state = LunchState()

  override def persistenceId: String = "lunch"

  override def receiveCommand: Receive = {
    case GetById(id) =>
      sender() ! state(id)
    case GetAll =>
      val values = state.lunches.values
      if (values.isEmpty) {
        sender() ! None
      } else {
        sender() ! Some(Lunches(values.toSeq))
      }
    case createCmd: Create =>
      val lunch = createCmd.lunch
      if (state.contains(lunch.id)) {
        sender() ! None
      } else {
        saveState(lunch.id, lunch)
      }
    case updateCmd: Update =>
      val lunch = updateCmd.lunch
      if (state.contains(updateCmd.id)) {
        saveState(updateCmd.id, lunch)
      } else {
        sender() ! None
      }
    case deleteCmd: DeleteById =>
      val id = deleteCmd.id
      if (state.contains(id)) {
        state -= id
        persistAsync(deleteCmd) { _ => {
          sender() ! Some(deleteCmd)
          if (lastSequenceNr != 0 && lastSequenceNr % 1000 == 0) {
            saveSnapshot(state)
          }
        }
        }
      } else {
        sender() ! None
      }
  }

  private def saveState(id: UUID, lunch: Lunch): Unit = {
    val newLunch = lunch.copy(id = id)
    state += (id, newLunch)
    persistAsync(newLunch) { _ => {
      sender() ! Some(newLunch)
      if (lastSequenceNr != 0 && lastSequenceNr % 1000 == 0) {
        saveSnapshot(state)
      }
    }
    }
  }

  override def receiveRecover: Receive = {
    case event: Lunch =>
      state += (event.id, event)
    case deleteCmd: DeleteById =>
      state -= deleteCmd.id
    case SnapshotOffer(_, snapshot: LunchState) =>
      state = snapshot
  }

}
