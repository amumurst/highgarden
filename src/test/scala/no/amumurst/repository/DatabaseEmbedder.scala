package no.amumurst.repository

import cats.effect._
import doobie.util.transactor.Transactor
import io.zonky.test.db.postgres.embedded._
import org.specs2.execute.Result

object DatabaseEmbedder {

  lazy val provider: PreparedDbProvider =
    PreparedDbProvider.forPreparer(FlywayPreparer.forClasspathLocation("classpath:db/migration"))

  private val databaseConnection = Resource.make(
    IO(
      provider
        .createDataSourceFromConnectionInfo(provider.createNewDatabase)
        .getConnection
    )
  )(c => IO(c.close()))

  val transactor: Resource[IO, Transactor[IO]] =
    databaseConnection.map(Transactor.fromConnection[IO])

  def databaseTest(testFunc: Transactor[IO] => IO[Result]): IO[Result] =
    DatabaseEmbedder.transactor.use(testFunc)
}
