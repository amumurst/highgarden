package no.amumurst

import cats.effect._
import no.amumurst.repository._
import no.amumurst.services._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.{Router, Server}

object Main extends IOApp {

  val createServer: Resource[IO, Server[IO]] =
    for {
      xa        <- Database.embeddedTransactor
      carService = CarEndpoints(CarRepository(xa))
      httpApp    = Router("/cars" -> carService).orNotFound
      server <- BlazeServerBuilder[IO](executionContext)
                  .bindHttp(8080, "0.0.0.0")
                  .withHttpApp(httpApp)
                  .resource
    } yield server

  override def run(args: List[String]): IO[ExitCode] =
    createServer.use(_ => IO.never).as(ExitCode.Success)
}
