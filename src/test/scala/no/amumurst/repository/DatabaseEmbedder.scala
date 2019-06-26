package no.amumurst.repository

import cats.effect._
import io.zonky.test.db.postgres.embedded._
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.specs2.execute.Result

import scala.concurrent.ExecutionContext

object DatabaseEmbedder {

  implicit val dsss: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  lazy val provider: PreparedDbProvider = PreparedDbProvider.forPreparer(
    FlywayPreparer.forClasspathLocation("classpath:db/migration"))

  private val databaseConnection = Resource.make(
    IO(
      provider
        .createDataSourceFromConnectionInfo(provider.createNewDatabase)
        .getConnection))(c => IO(c.close()))

  val transactor: Resource[IO, Transactor[IO]] =
    for {
      con <- databaseConnection
      te  <- ExecutionContexts.cachedThreadPool[IO]
    } yield Transactor.fromConnection[IO](con, te)

  def databaseTest(testFunc: Transactor[IO] => IO[Result]): Result =
    DatabaseEmbedder.transactor.use(testFunc).unsafeRunSync()
}
