package com.vitornp.choice.lunch.model

import com.fasterxml.jackson.core.`type`.TypeReference

object Weight extends Enumeration {
  type Weight = Value
  val Low, Medium, High = Value
}

class WeightType extends TypeReference[Weight.type]
