package no.amumurst
package server
package services

import cats.effect._
import cats.implicits._
import no.amumurst.transaction.DataTransactor
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

case class CarService[F[_]: Effect](transactor: DataTransactor[F])
    extends Http4sDsl[F] {

  private val base = Root / "cars"
  private val repo = transactor.carRepo

  implicit val carDecoder: EntityDecoder[F, Car] = jsonOf[F, Car]
  implicit val carListDecoder: EntityDecoder[F, List[Car]] =
    jsonOf[F, List[Car]]
  implicit val carEncoder: EntityEncoder[F, Car] = jsonEncoderOf[F, Car]
  implicit val carListEncoder: EntityEncoder[F, List[Car]] =
    jsonEncoderOf[F, List[Car]]

  val service = HttpService[F] {
    case GET -> `base` =>
      Ok.apply(repo.getAllCars)
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
