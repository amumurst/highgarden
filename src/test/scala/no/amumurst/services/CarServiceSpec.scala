package no.amumurst.services

import cats.effect._
import no.amumurst.domain.Car
import no.amumurst.repository.CarRepositoryAlg
import org.http4s._
import org.http4s.implicits._
import org.specs2.mutable.Specification

class CarServiceSpec extends Specification {
  import CarServiceSpecData._

  "/" should {
    "GET" in {
      "responds OK" in {
        val repo    = new EmptyCarRepo {}
        val service = new CarService(repo).service.orNotFound
        val request = Request[IO](method = Method.GET, uri = uri"/")

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.Ok)
      }
    }
    "POST" in {
      val repo = new EmptyCarRepo {
        override def insertCar(car: Car): IO[Either[Throwable, Car]] =
          IO(
            Either.cond(car.id == 12, car, new Throwable("badstuff"))
          )
      }
      val service = new CarService(repo).service.orNotFound
      "responds ok" in {
        val request = Request[IO](method = Method.POST, uri = uri"/")
          .withEntity(emptyCar)

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.Ok)
      }
      "responds internalServerError when bad stuff happens" in {
        val request = Request[IO](method = Method.POST, uri = uri"/")
          .withEntity(emptyCar.copy(id = 1))

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.InternalServerError)
      }
    }
    "PUT" in {
      val repo = new EmptyCarRepo {
        override def insertCar(car: Car): IO[Either[Throwable, Car]] =
          IO(Either.cond(car.id == 12, car, new Throwable("badstuff")))
      }
      val service = new CarService(repo).service.orNotFound

      "responds ok" in {
        val request = Request[IO](method = Method.PUT, uri = uri"/")
          .withEntity(List(emptyCar, emptyCar))

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.Ok)
      }
      "responds internalServerError when bad stuff happens with one car" in {
        val request = Request[IO](method = Method.PUT, uri = uri"/")
          .withEntity(List(emptyCar, emptyCar.copy(id = 1)))

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.InternalServerError)
      }
    }
    "DELETE" in {
      val repo    = new EmptyCarRepo {}
      val service = new CarService(repo).service.orNotFound
      "responds ok" in {
        val request = Request[IO](method = Method.DELETE, uri = uri"/1")

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.Ok)
      }
    }
  }

  "/id " should {
    "GET" in {
      val repo = new EmptyCarRepo {
        override def getCar(id: Long): IO[Option[Car]] =
          IO(Some(emptyCar).filter(_ => id == 12))
      }
      val service = new CarService(repo).service.orNotFound
      "responds notFound when asking for something not in database" in {
        val request = Request[IO](method = Method.GET, uri = uri"/45")

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.NotFound)
      }
      "responds ok when asking for something not in database" in {
        val request = Request[IO](method = Method.GET, uri = uri"/12")

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.Ok)
      }

      "responds notFound when asking for string id" in {
        val request = Request[IO](method = Method.GET, uri = uri"/askd")

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.NotFound)
      }
    }
    "PATCH" in {
      val repo = new EmptyCarRepo {
        override def updateCar(car: Car): IO[Either[Throwable, Car]] =
          IO(
            Either.cond(car.id == 12, car, new Throwable("badstuff"))
          )
      }
      val service = new CarService(repo).service.orNotFound
      "responds ok" in {
        val request = Request[IO](method = Method.PATCH, uri = uri"/12")
          .withEntity(emptyCar)

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.Ok)
      }
      "responds ok even if id in body is wrong" in {
        val request = Request[IO](method = Method.PATCH, uri = uri"/12")
          .withEntity(emptyCar.copy(id = 1))

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.Ok)
      }
      "responds internalServerError when bad stuff happens" in {
        val request = Request[IO](method = Method.PATCH, uri = uri"/11")
          .withEntity(emptyCar)

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.InternalServerError)
      }
    }
    "PUT" in {
      val repo = new EmptyCarRepo {
        override def updateCar(car: Car): IO[Either[Throwable, Car]] =
          IO(Either.cond(car.id == 12, car, new Throwable("badstuff")))
        override def insertCar(car: Car): IO[Either[Throwable, Car]] =
          IO(Either.cond(car.id == 12, car, new Throwable("badstuff")))
        override def getCar(id: Long): IO[Option[Car]] =
          IO(Some(emptyCar).filter(_.id == id))
      }
      val service = new CarService(repo).service.orNotFound

      "responds ok" in {
        val request = Request[IO](method = Method.PUT, uri = uri"/12")
          .withEntity(emptyCar)

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.Ok)
      }
      "responds internalServerError when bad stuff happens" in {
        val request = Request[IO](method = Method.PUT, uri = uri"/1")
          .withEntity(emptyCar.copy(id = 1))

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.InternalServerError)
      }
    }
    "DELETE" in {
      val repo    = new EmptyCarRepo {}
      val service = new CarService(repo).service.orNotFound
      "responds ok" in {
        val request = Request[IO](method = Method.DELETE, uri = uri"/1")

        val response = service.run(request).unsafeRunSync

        response.status must beEqualTo(Status.Ok)
      }
    }

  }

}

object CarServiceSpecData {
  trait EmptyCarRepo extends CarRepositoryAlg[IO] {
    def getAllCars: IO[List[Car]]                       = IO(List.empty)
    def getCar(id: Long): IO[Option[Car]]               = IO(None)
    def insertCar(car: Car): IO[Either[Throwable, Car]] = IO(Right(car))
    def updateCar(car: Car): IO[Either[Throwable, Car]] = IO(Right(car))
    def deleteCar(id: Long): IO[Unit]                   = IO(())
    def deleteCars: IO[Unit]                            = IO(())
  }
  val emptyCar = Car(12, "plate", "red", Some("CaryMcCarface"))
}
