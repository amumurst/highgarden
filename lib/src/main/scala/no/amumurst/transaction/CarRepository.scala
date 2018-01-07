package no.amumurst.transaction
import cats.Monad
import doobie._

case class CarRepository[F[_]: Monad](transactor: Transactor[F]) {
  import doobie.implicits._

  def getAllCars: F[List[Car]] = SQLQueries.allCars.transact(transactor)

  object SQLQueries {
    import doobie.implicits._
    def allCars: ConnectionIO[List[Car]] =
      sql"""select * from car""".query[Car].list
  }
}
