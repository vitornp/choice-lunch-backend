package com.vitornp.choice.lunch.model

import com.vitornp.choice.lunch.model.Weight._

final case class Lunch(name: String, time: Weight, price: Weight)

final case class Lunchs(lunchs: Seq[Lunch])
