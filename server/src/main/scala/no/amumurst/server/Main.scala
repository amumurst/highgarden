package no.amumurst
package server

import javax.sql.DataSource

import cats.effect.IO
import com.opentable.db.postgres.embedded.{EmbeddedPostgres, FlywayPreparer}
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import no.amumurst.server.services.CarService
import no.amumurst.transaction.DataTransactor
import org.http4s.server.blaze._

object Main extends StreamApp[IO] {
  def migrate(dataSource: DataSource) =
    FlywayPreparer
      .forClasspathLocation("classpath:db/migration")
      .prepare(dataSource)

  override def stream(args: List[String],
                      requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {

    val transactor = DataTransactor[IO](
      EmbeddedPostgres.builder().setPort(5551).start().getPostgresDatabase)

    migrate(transactor.dataSource)
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(CarService(transactor).service, "/")
      .serve
  }
}
