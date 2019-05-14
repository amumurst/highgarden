package no.amumurst
package server
package services

import cats.effect._
import cats.implicits._
import com.opentable.db.postgres.embedded.{EmbeddedPostgres, FlywayPreparer}
import doobie.{ExecutionContexts, Transactor}
import javax.sql.DataSource
import no.amumurst.transaction.CarRepository
import org.http4s.server.{Router, Server}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

object HighGardenServer {

  def createServer[F[_]: ContextShift: ConcurrentEffect: Timer]
    : Resource[F, Server[F]] =
    for {
      db         <- Database.createEmbedded[F]
      xa         <- Database.transactor[F](db)
      carService = CarService[F](CarRepository(xa)).service
      httpApp    = Router("/" -> carService).orNotFound
      server <- BlazeServerBuilder[F]
                 .bindHttp(8080, "0.0.0.0")
                 .withHttpApp(httpApp)
                 .resource
    } yield server

}

object Database {
  def transactor[F[_]: Async: ContextShift](
      ds: DataSource,
      ceSize: Int = 32): Resource[F, Transactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](ceSize)
      te <- ExecutionContexts.cachedThreadPool[F]
    } yield Transactor.fromDataSource[F](ds, ce, te)

  def migrate[F[_]: Sync](dataSource: DataSource): F[Unit] =
    Sync[F].delay(
      FlywayPreparer
        .forClasspathLocation("classpath:db/migration")
        .prepare(dataSource))

  def createEmbedded[F[_]: Sync]: Resource[F, DataSource] =
    for {
      embeddedDb <- Resource.make(
                     Sync[F].delay(EmbeddedPostgres.builder().start()))(s =>
                     Sync[F].delay(s.close()))
      db = embeddedDb.getPostgresDatabase
      _  <- Resource.liftF(migrate(db))

    } yield db

}
