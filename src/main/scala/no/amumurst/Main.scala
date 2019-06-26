package no.amumurst

import no.amumurst.repository._
import no.amumurst.services._
import cats.effect._
import cats.implicits._
import org.http4s.server.{Router, Server}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

object Main extends IOApp {

  val createServer: Resource[IO, Server[IO]] =
    for {
      db         <- Database.createEmbedded
      xa         <- Database.transactor(db)
      carService = CarService(CarRepository(xa)).service
      httpApp    = Router("/cars" -> carService).orNotFound
      server <- BlazeServerBuilder[IO]
                 .bindHttp(8080, "0.0.0.0")
                 .withHttpApp(httpApp)
                 .resource
    } yield server

  override def run(args: List[String]): IO[ExitCode] =
    createServer.use(_ => IO.never).as(ExitCode.Success)
}
