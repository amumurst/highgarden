package no.amumurst.server

import cats.effect._
import io.circe.syntax._
import no.amumurst.transaction.DataTransactor
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
case class CarService(transactor: DataTransactor[IO]) {

  def cars = transactor.carRepo.getAllCars

  val service = HttpService[IO] {
    case GET -> Root => Ok("Hello from carservice")
    case GET -> Root / "cars" =>
      Ok(cars.map(_.asJson))
  }
}
