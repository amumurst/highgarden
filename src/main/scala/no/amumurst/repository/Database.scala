package no.amumurst
package repository

import cats.effect._
import doobie._
import io.zonky.test.db.postgres.embedded.{EmbeddedPostgres, FlywayPreparer}

import javax.sql.DataSource

object Database {
  def transactor(ds: DataSource, ceSize: Int = 32)(
      implicit cs: ContextShift[IO]): Resource[IO, Transactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](ceSize)
      bl <- Blocker[IO]
    } yield Transactor.fromDataSource[IO](ds, ce, bl)

  def migrate(dataSource: DataSource): IO[Unit] =
    IO(FlywayPreparer.forClasspathLocation("classpath:db/migration"))
      .map(_.prepare(dataSource))

  val createEmbedded: Resource[IO, DataSource] =
    Resource
      .make(IO(EmbeddedPostgres.builder().start()))(s => IO(s.close()))
      .map(_.getPostgresDatabase)
      .evalTap(migrate)

  def embeddedTransactor(
      implicit cs: ContextShift[IO]): Resource[IO, Transactor[IO]] =
    for {
      ds <- createEmbedded
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
      bl <- Blocker[IO]
    } yield Transactor.fromDataSource[IO](ds, ce, bl)

}
