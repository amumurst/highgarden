package no.amumurst.http

import cats.effect._
import cats.implicits._
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import no.amumurst.domain.Car
import no.amumurst.repository.CarRepository
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe.CirceEntityCodec._

object CarEndpoints:
  case class CarJson(id: Long, licenseNumber: String, color: String, name: Option[String]) derives Codec.AsObject {
    lazy val asDomain: Car = Car(id, licenseNumber, color, name)
  }
  object CarJson:
    def apply(car: Car): CarJson = CarJson(car.id, car.licenseNumber, car.color, car.name)

  private def carResponse(e: Either[Throwable, Car]): IO[Response[IO]] =
    e match {
      case Right(car) => Ok(CarJson(car))
      case Left(err)  => InternalServerError(err.getMessage)
    }

  def apply(repo: CarRepository): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root =>
        Ok.apply(repo.getAllCars.map(_.map(CarJson.apply)))
      case GET -> Root / LongVar(id) =>
        repo
          .getCar(id)
          .flatMap(_.fold(NotFound(s"Car with id $id not found"))(car => Ok(CarJson(car))))

      case req @ POST -> Root =>
        req
          .as[CarJson]
          .flatMap(c => repo.insertCar(c.asDomain))
          .flatMap(carResponse)

      case req @ PATCH -> Root / LongVar(id) =>
        req
          .as[CarJson]
          .flatMap(c => repo.updateCar(c.asDomain.copy(id = id)))
          .flatMap(carResponse)

      case req @ PUT -> Root / LongVar(id) =>
        for
          car      <- req.as[CarJson]
          exists   <- repo.getCar(id)
          inserted <- exists.fold(repo.insertCar(car.asDomain))(_ => repo.updateCar(car.asDomain))
          response <- carResponse(inserted)
        yield response

      case req @ PUT -> Root =>
        for
          cars       <- req.as[List[CarJson]].map(_.map(_.asDomain))
          _          <- repo.deleteCars
          inserted   <- cars.traverse(repo.insertCar)
          (errs, oks) = inserted.separate
          response <- errs match {
                        case Nil => Ok(oks.map(CarJson.apply))
                        case err =>
                          InternalServerError(err.map(_.getMessage).mkString("/n"))
                      }
        yield response

      case DELETE -> Root / LongVar(id) =>
        repo.deleteCar(id).flatMap(Ok(_))
      case DELETE -> Root =>
        repo.deleteCars.flatMap(Ok(_))
    }
