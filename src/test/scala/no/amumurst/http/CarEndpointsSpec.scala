package no.amumurst.http

import cats.effect._
import cats.effect.testing.specs2.CatsEffect
import no.amumurst.domain.Car
import no.amumurst.http.CarEndpoints.CarJson
import no.amumurst.repository.CarRepository
import org.http4s._
import org.http4s.implicits._
import org.http4s.circe.CirceEntityCodec._
import org.specs2.mutable.Specification

class CarEndpointsSpec extends Specification with CatsEffect {
  import CarServiceSpecData._

  "/" should {
    "GET" in {
      "responds OK" in {
        val repo    = new EmptyCarRepo {}
        val service = CarEndpoints(repo).orNotFound
        val request = Request[IO](method = Method.GET, uri = uri"/")

        service.run(request).map { response =>
          response.status must beEqualTo(Status.Ok)
        }
      }
    }
    "POST" in {
      val repo = new EmptyCarRepo {
        override def insertCar(car: Car): IO[Either[Throwable, Car]] =
          IO(
            Either.cond(car.id == 12, car, new Throwable("badstuff"))
          )
      }
      val service = CarEndpoints(repo).orNotFound
      "responds ok" in {
        val request = Request[IO](method = Method.POST, uri = uri"/")
          .withEntity(CarJson(emptyCar))

        service.run(request).map { response =>
          response.status must beEqualTo(Status.Ok)
        }
      }
      "responds internalServerError when bad stuff happens" in {
        val request = Request[IO](method = Method.POST, uri = uri"/")
          .withEntity(CarJson(emptyCar).copy(id = 1))

        service.run(request).map { response =>
          response.status must beEqualTo(Status.InternalServerError)
        }
      }
    }
    "PUT" in {
      val repo = new EmptyCarRepo {
        override def insertCar(car: Car): IO[Either[Throwable, Car]] =
          IO(Either.cond(car.id == 12, car, new Throwable("badstuff")))
      }
      val service = CarEndpoints(repo).orNotFound

      "responds ok" in {
        val request = Request[IO](method = Method.PUT, uri = uri"/")
          .withEntity(List(CarJson(emptyCar), CarJson(emptyCar)))

        service.run(request).map { response =>
          response.status must beEqualTo(Status.Ok)
        }
      }
      "responds internalServerError when bad stuff happens with one car" in {
        val request = Request[IO](method = Method.PUT, uri = uri"/")
          .withEntity(List(CarJson(emptyCar), CarJson(emptyCar).copy(id = 1)))

        service.run(request).map { response =>
          response.status must beEqualTo(Status.InternalServerError)
        }
      }
    }
    "DELETE" in {
      val repo    = new EmptyCarRepo {}
      val service = CarEndpoints(repo).orNotFound
      "responds ok" in {
        val request = Request[IO](method = Method.DELETE, uri = uri"/1")

        service.run(request).map { response =>
          response.status must beEqualTo(Status.Ok)
        }
      }
    }
  }

  "/id " should {
    "GET" in {
      val repo = new EmptyCarRepo {
        override def getCar(id: Long): IO[Option[Car]] =
          IO(Some(emptyCar).filter(_ => id == 12))
      }
      val service = CarEndpoints(repo).orNotFound
      "responds notFound when asking for something not in database" in {
        val request = Request[IO](method = Method.GET, uri = uri"/45")

        service.run(request).map { response =>
          response.status must beEqualTo(Status.NotFound)
        }
      }
      "responds ok when asking for something not in database" in {
        val request = Request[IO](method = Method.GET, uri = uri"/12")

        service.run(request).map { response =>
          response.status must beEqualTo(Status.Ok)
        }
      }

      "responds notFound when asking for string id" in {
        val request = Request[IO](method = Method.GET, uri = uri"/askd")

        service.run(request).map { response =>
          response.status must beEqualTo(Status.NotFound)
        }
      }
    }
    "PATCH" in {
      val repo = new EmptyCarRepo {
        override def updateCar(car: Car): IO[Either[Throwable, Car]] =
          IO(
            Either.cond(car.id == 12, car, new Throwable("badstuff"))
          )
      }
      val service = CarEndpoints(repo).orNotFound
      "responds ok" in {
        val request = Request[IO](method = Method.PATCH, uri = uri"/12")
          .withEntity(CarJson(emptyCar))

        service.run(request).map { response =>
          response.status must beEqualTo(Status.Ok)
        }
      }
      "responds ok even if id in body is wrong" in {
        val request = Request[IO](method = Method.PATCH, uri = uri"/12")
          .withEntity(CarJson(emptyCar).copy(id = 1))

        service.run(request).map { response =>
          response.status must beEqualTo(Status.Ok)
        }
      }
      "responds internalServerError when bad stuff happens" in {
        val request = Request[IO](method = Method.PATCH, uri = uri"/11")
          .withEntity(CarJson(emptyCar))

        service.run(request).map { response =>
          response.status must beEqualTo(Status.InternalServerError)
        }
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
      val service = CarEndpoints(repo).orNotFound

      "responds ok" in {
        val request = Request[IO](method = Method.PUT, uri = uri"/12")
          .withEntity(CarJson(emptyCar))

        service.run(request).map { response =>
          response.status must beEqualTo(Status.Ok)
        }
      }
      "responds internalServerError when bad stuff happens" in {
        val request = Request[IO](method = Method.PUT, uri = uri"/1")
          .withEntity(CarJson(emptyCar).copy(id = 1))

        service.run(request).map { response =>
          response.status must beEqualTo(Status.InternalServerError)
        }
      }
    }
    "DELETE" in {
      val repo    = new EmptyCarRepo {}
      val service = CarEndpoints(repo).orNotFound
      "responds ok" in {
        val request = Request[IO](method = Method.DELETE, uri = uri"/1")

        service.run(request).map { response =>
          response.status must beEqualTo(Status.Ok)
        }
      }
    }

  }

}

object CarServiceSpecData {
  trait EmptyCarRepo extends CarRepository {
    def getAllCars: IO[List[Car]]                       = IO(List.empty)
    def getCar(id: Long): IO[Option[Car]]               = IO(None)
    def insertCar(car: Car): IO[Either[Throwable, Car]] = IO(Right(car))
    def updateCar(car: Car): IO[Either[Throwable, Car]] = IO(Right(car))
    def deleteCar(id: Long): IO[Unit]                   = IO(())
    def deleteCars: IO[Unit]                            = IO(())
  }
  val emptyCar = Car(12, "plate", "red", Some("CaryMcCarface"))
}
