package no.amumurst
package transaction

import javax.sql.DataSource

import cats.effect.Async
import doobie._
import io.circe.generic.JsonCodec

@JsonCodec case class Car(id: Long,
                          licenseNumber: String,
                          color: String,
                          name: Option[String])

case class DataTransactor[F[_]: Async](dataSource: DataSource) {
  private val transactor = Transactor.fromDataSource[F](dataSource)

  val carRepo = CarRepository[F](transactor)

}
