package no.amumurst
package server
package services

import javax.sql.DataSource

import cats.effect.Effect
import com.opentable.db.postgres.embedded.{EmbeddedPostgres, FlywayPreparer}
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import no.amumurst.transaction.DataTransactor
import org.http4s.server.blaze._

case class HighGarden[F[_]: Effect]() extends StreamApp[F] {
  def migrate(dataSource: DataSource) =
    FlywayPreparer
      .forClasspathLocation("classpath:db/migration")
      .prepare(dataSource)

  override def stream(args: List[String],
                      requestShutdown: F[Unit]): Stream[F, ExitCode] = {

    val transactor =
      DataTransactor[F](EmbeddedPostgres.builder().start().getPostgresDatabase)

    migrate(transactor.dataSource)
    BlazeBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .mountService(CarService[F](transactor).service, "/")
      .serve
  }
}
