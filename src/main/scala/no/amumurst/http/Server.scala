package no.amumurst.http

import cats.effect.{IO, Resource}
import no.amumurst.repository.CarRepository
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.{Router, Server}

object Server {
  def start(carRepository: CarRepository): Resource[IO, Server] = {
    val httpApp = Router("/cars" -> CarEndpoints(carRepository)).orNotFound
    EmberServerBuilder
      .default[IO]
      .withHttpApp(httpApp)
      .build
  }
}
