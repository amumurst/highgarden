package no.amumurst

import io.circe.generic.JsonCodec

@JsonCodec case class Car(id: Long,
                          licenseNumber: String,
                          color: String,
                          name: Option[String])
