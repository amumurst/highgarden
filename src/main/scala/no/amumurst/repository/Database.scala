package no.amumurst
package repository

import cats.effect._
import doobie.{ExecutionContexts, Transactor}
import io.zonky.test.db.postgres.embedded.{EmbeddedPostgres, FlywayPreparer}
import javax.sql.DataSource

object Database {
  def transactor(ds: DataSource, ceSize: Int = 32)(
      implicit cs: ContextShift[IO]): Resource[IO, Transactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](ceSize)
      te <- ExecutionContexts.cachedThreadPool[IO]
    } yield Transactor.fromDataSource[IO](ds, ce, te)

  def migrate(dataSource: DataSource): IO[Unit] =
    IO(
      FlywayPreparer
        .forClasspathLocation("classpath:db/migration")
        .prepare(dataSource))

  val createEmbedded: Resource[IO, DataSource] =
    for {
      embeddedDb <- Resource.make(IO(EmbeddedPostgres.builder().start()))(s =>
                     IO(s.close()))
      db = embeddedDb.getPostgresDatabase
      _  <- Resource.liftF(migrate(db))
    } yield db

}
