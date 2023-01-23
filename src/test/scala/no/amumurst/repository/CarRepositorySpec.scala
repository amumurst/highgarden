package no.amumurst.repository

import cats.effect.testing.specs2.CatsEffect
import no.amumurst.domain.Car
import org.specs2.mutable.Specification

class CarRepositorySpec extends Specification with CatsEffect {

  import DatabaseEmbedder.databaseTest

  "CarRepository" should {
    "get non existent car is None" in databaseTest { t =>
      val repo = CarRepository(t)
      for {
        carOne <- repo.getCar(1)
      } yield carOne must beNone
    }
    "Get newly inserted car is found" in databaseTest { t =>
      val repo = CarRepository(t)
      for {
        _      <- repo.insertCar(Car(1, "23", "1d", None))
        carOne <- repo.getCar(1)
      } yield carOne must beSome[Car].which(_.id must beEqualTo(1))
    }
  }
}
