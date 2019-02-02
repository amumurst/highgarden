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
      ce         <- ExecutionContexts.fixedThreadPool[F](32)
      te         <- ExecutionContexts.cachedThreadPool[F]
      xa         = Transactor.fromDataSource[F](db.getPostgresDatabase, ce, te)
      carService = CarService[F](CarRepository(xa)).service
      httpApp    = Router("/" -> carService).orNotFound
      _          <- Resource.liftF(Database.migrate(xa.kernel))
      server <- BlazeServerBuilder[F]
                 .bindHttp(8080, "0.0.0.0")
                 .withHttpApp(httpApp)
                 .resource
    } yield server

}

object Database {
  def migrate[F[_]](dataSource: DataSource)(implicit F: Sync[F]): F[Unit] =
    F.delay(
      FlywayPreparer
        .forClasspathLocation("classpath:db/migration")
        .prepare(dataSource))

  def createEmbedded[F[_]](implicit F: Sync[F]): Resource[F, EmbeddedPostgres] =
    Resource.make(F.delay(EmbeddedPostgres.builder().start()))(s =>
      F.delay(s.close()))
}
