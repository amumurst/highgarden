package no.amumurst
package domain

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class Car(id: Long, licenseNumber: String, color: String, name: Option[String])

object Car {
  implicit val carJsonCodec: Codec[Car] = deriveCodec
}
