package com.vitornp.choice.lunch.route

import akka.http.scaladsl.server.{Directives, Route}

object Healthcheck extends Directives {

  def apply(): Route = path("healthcheck") {
    get {
      complete("ok")
    }
  }

}
