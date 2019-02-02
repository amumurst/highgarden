package no.amumurst
package server

import services.HighGardenServer
import cats.effect._
import cats.implicits._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    HighGardenServer.createServer[IO].use(_ => IO.never).as(ExitCode.Success)
}
