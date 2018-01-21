package no.amumurst
package transaction

import javax.sql.DataSource

import cats.effect.Effect
import doobie._

case class DataTransactor[F[_]: Effect](dataSource: DataSource) {
  private val transactor = Transactor.fromDataSource[F](dataSource)

  val carRepo = CarRepository[F](transactor)

}
