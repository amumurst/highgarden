package no.amumurst
package services

import cats.effect._
import cats.implicits._
import no.amumurst.domain.Car
import no.amumurst.repository.CarRepositoryAlg
import org.http4s._
import org.http4s.dsl.io._

class CarService(repo: CarRepositoryAlg[IO]) {
  private def carResponse(e: Either[Throwable, Car]): IO[Response[IO]] =
    e match {
      case Right(car) => Ok(car)
      case Left(err)  => InternalServerError(err.getMessage)
    }

  val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok.apply(repo.getAllCars)
    case GET -> Root / LongVar(id) =>
      repo
        .getCar(id)
        .flatMap(
          _.fold(NotFound(s"Car with id $id not found"))(Ok(_))
        )

    case req @ POST -> Root =>
      req.as[Car].flatMap(c => repo.insertCar(c)).flatMap(carResponse)

    case req @ PATCH -> Root / LongVar(id) =>
      req
        .as[Car]
        .flatMap(c => repo.updateCar(c.copy(id = id)))
        .flatMap(carResponse)

    case req @ PUT -> Root / LongVar(id) =>
      for {
        car      <- req.as[Car]
        exists   <- repo.getCar(id)
        inserted <- exists.fold(repo.insertCar(car))(_ => repo.updateCar(car))
        response <- carResponse(inserted)
      } yield response

    case req @ PUT -> Root =>
      for {
        cars     <- req.as[List[Car]]
        _        <- repo.deleteCars
        inserted <- cars.traverse(repo.insertCar)
        oks       = inserted.collect { case Right(r) => r }
        errs      = inserted.collect { case Left(err) => err }
        response <- errs match {
                      case Nil => Ok(oks)
                      case err =>
                        InternalServerError(err.map(_.getMessage).mkString("/n"))
                    }
      } yield response

    case DELETE -> Root / LongVar(id) =>
      repo.deleteCar(id).flatMap(Ok(_))
    case DELETE -> Root =>
      repo.deleteCars.flatMap(Ok(_))
  }
}
