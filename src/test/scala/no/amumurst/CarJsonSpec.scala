package no.amumurst.domain

import io.circe._
import io.circe.syntax._
import org.specs2.mutable.Specification

class CarJsonSpec extends Specification {

  val car = Car(12, "plate", "red", Some("CaryMcCarface"))

  val expectedJson = Json.obj(
    "id" := 12,
    "licenseNumber" := "plate",
    "color" := "red",
    "name" := "CaryMcCarface"
  )

  "encoder" in {
    car.asJson must beEqualTo(expectedJson)
  }
  "decoder" in {
    expectedJson.as[Car] must beRight { (c: Car) =>
      c must beEqualTo(car)
    }
  }
}
