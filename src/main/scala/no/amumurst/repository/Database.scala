package no.amumurst
package repository

import cats.effect._
import doobie._
import io.zonky.test.db.postgres.embedded.{EmbeddedPostgres, FlywayPreparer}

import javax.sql.DataSource

object Database {
  private def migrate(dataSource: DataSource): IO[Unit] =
    IO(FlywayPreparer.forClasspathLocation("classpath:db/migration"))
      .map(_.prepare(dataSource))

  private val createEmbedded: Resource[IO, DataSource] =
    Resource
      .make(IO(EmbeddedPostgres.builder().start()))(s => IO(s.close()))
      .map(_.getPostgresDatabase)
      .evalTap(migrate)

  val embeddedTransactor: Resource[IO, Transactor[IO]] =
    for {
      ds <- createEmbedded
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
    } yield Transactor.fromDataSource[IO](ds, ce)

}
