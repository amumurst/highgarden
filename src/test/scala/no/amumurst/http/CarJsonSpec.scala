package no.amumurst.http

import io.circe._
import io.circe.syntax._
import no.amumurst.domain.Car
import no.amumurst.http.CarEndpoints.CarJson
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
    CarJson(car).asJson must beEqualTo(expectedJson)
  }
  "decoder" in {
    expectedJson.as[CarJson].map(_.asDomain) must beRight { (c: Car) =>
      c must beEqualTo(car)
    }
  }
}
