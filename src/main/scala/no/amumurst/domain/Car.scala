package no.amumurst
package domain

import cats.effect.IO
import io.circe._
import io.circe.generic.semiauto._
import org.http4s.circe._
import org.http4s._

case class Car(id: Long,
               licenseNumber: String,
               color: String,
               name: Option[String])

object Car {
  implicit val carJsonEncoder: Encoder[Car] = deriveEncoder
  implicit val carJsonDecoder: Decoder[Car] = deriveDecoder

  implicit val carEntityDecoder: EntityDecoder[IO, Car] = jsonOf[IO, Car]
  implicit val carListEntityDecoder: EntityDecoder[IO, List[Car]] =
    jsonOf[IO, List[Car]]
  implicit val carEntityEncoder: EntityEncoder[IO, Car] = jsonEncoderOf[IO, Car]
  implicit val carListEntityEncoder: EntityEncoder[IO, List[Car]] =
    jsonEncoderOf[IO, List[Car]]

}
