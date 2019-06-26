package no.amumurst.repository

import cats.implicits._
import no.amumurst.domain.Car
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext

class CarRepositorySpec extends Specification {

  import DatabaseEmbedder.databaseTest

  /*"CarRepository" should {
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
      } yield carOne must beSome.which(_.id must beEqualTo(1))
    }
  }*/
}
