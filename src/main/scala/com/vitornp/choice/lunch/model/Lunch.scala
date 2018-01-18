package com.vitornp.choice.lunch.model

import java.util.UUID

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration
import com.vitornp.choice.lunch.model.Weight._

final case class Lunch(
  id: UUID,
  name: String,
  time: Weight,
  price: Weight
) {

  @JsonCreator
  def this(
    name: String,
    @JsonScalaEnumeration(classOf[WeightType]) time: Weight,
    @JsonScalaEnumeration(classOf[WeightType]) price: Weight
  ) = this(UUID.randomUUID(), name, time, price)

}

final case class Lunches(lunches: Seq[Lunch]) {
  def this() = this(Seq.empty)
}
