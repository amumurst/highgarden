package no.amumurst.server

import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

import cats.effect._
import cats.implicits._
import com.opentable.db.postgres.embedded._
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import no.amumurst.Car
import no.amumurst.transaction.CarRepository
import org.specs2.execute.Result
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext

class EmbeddedPG extends Specification {

  implicit val dsss: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  import DatabaseEmbedder.databaseTest

  "a" should {
    "1" in databaseTest { t =>
      val repo = CarRepository(t)
      for {
        carOne <- repo.getCar(1)
      } yield carOne must beNone
    }
    "2" in databaseTest { t =>
      val repo = CarRepository(t)
      for {
        _      <- repo.insertCar(Car(1, "23", "1d", None))
        carOne <- repo.getCar(1)
      } yield carOne must beSome.which(_.id must beEqualTo(1))

    }
    "3" in databaseTest { t =>
      val repo = CarRepository(t)
      for {
        carOne <- repo.getCar(1)
      } yield carOne must beNone
    }
  }

}

object DatabaseEmbedder {

  val provider: PreparedDbProvider = PreparedDbProvider.forPreparer(
    FlywayPreparer.forClasspathLocation("classpath:db/migration"),
    new CopyOnWriteArrayList[Consumer[EmbeddedPostgres.Builder]])

  private val databaseConnection = Resource.make(
    IO.delay(
      provider
        .createDataSourceFromConnectionInfo(provider.createNewDatabase)
        .getConnection))(c => IO.delay(c.close()))

  def transactor(implicit c: ContextShift[IO]): Resource[IO, Transactor[IO]] =
    for {
      con <- databaseConnection
      te  <- ExecutionContexts.cachedThreadPool[IO]
    } yield Transactor.fromConnection[IO](con, te)

  def databaseTest(testFunc: Transactor[IO] => IO[Result])(
      implicit c: ContextShift[IO]): Result =
    DatabaseEmbedder.transactor.use(testFunc).unsafeRunSync()
}
