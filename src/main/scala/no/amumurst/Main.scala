package no.amumurst

import cats.effect.{IO, IOApp}
import no.amumurst.repository.{CarRepository, Database}
import no.amumurst.http.Server

object Main extends IOApp.Simple {
  val makeServer =
    for {
      xa     <- Database.embeddedTransactor
      carRepo = CarRepository(xa)
      server <- Server.start(carRepo)
    } yield server
  override def run: IO[Unit] = makeServer.useForever
}
