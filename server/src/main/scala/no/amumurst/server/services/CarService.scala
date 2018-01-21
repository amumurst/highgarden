package no.amumurst
package server
package services

import cats.effect._
import cats.implicits._
import no.amumurst.transaction.DataTransactor
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._

case class CarService(transactor: DataTransactor[IO]) {

  private val base = Root / "cars"
  private val repo = transactor.carRepo

  implicit val carDecoder: EntityDecoder[IO, Car] = jsonOf[IO, Car]
  implicit val carListDecoder: EntityDecoder[IO, List[Car]] =
    jsonOf[IO, List[Car]]
  implicit val carEncoder: EntityEncoder[IO, Car] = jsonEncoderOf[IO, Car]
  implicit val carListEncoder: EntityEncoder[IO, List[Car]] =
    jsonEncoderOf[IO, List[Car]]

  val service = HttpService[IO] {
    case GET -> `base` =>
      Ok(repo.getAllCars)
    case GET -> `base` / LongVar(id) =>
      repo
        .getCar(id)
        .flatMap(
          _.fold(NotFound(s"Car with id $id not found"))(Ok(_))
        )

    case req @ POST -> `base` =>
      req.as[Car].flatMap(c => repo.insertCar(c)).flatMap(Ok(_))

    case req @ PATCH -> `base` / LongVar(id) =>
      req.as[Car].flatMap(c => repo.updateCar(c)).flatMap(Ok(_))

    case req @ PUT -> `base` / LongVar(id) =>
      for {
        car      <- req.as[Car]
        exists   <- repo.getCar(id)
        inserted <- exists.fold(repo.insertCar(car))(_ => repo.updateCar(car))
        response <- Ok(inserted)
      } yield response

    case req @ PUT -> `base` =>
      for {
        cars     <- req.as[List[Car]]
        _        <- repo.deleteCars
        inserted <- cars.map(repo.insertCar).sequence
        response <- Ok(inserted)
      } yield response

    case DELETE -> `base` / LongVar(id) =>
      repo.deleteCar(id).flatMap(Ok(_))
    case DELETE -> `base` =>
      repo.deleteCars.flatMap(Ok(_))
  }
}
