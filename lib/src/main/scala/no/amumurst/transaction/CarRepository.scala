package no.amumurst
package transaction
import cats.Monad
import doobie._

case class CarRepository[F[_]: Monad](transactor: Transactor[F]) {
  import doobie.implicits._

  def getAllCars: F[List[Car]] =
    SQLQueries.allCars.transact(transactor)
  def getCar(id: Long): F[Option[Car]] =
    SQLQueries.getCar(id).transact(transactor)
  def insertCar(car: Car): F[Car] =
    SQLQueries.insertCar(car).transact(transactor)
  def updateCar(car: Car): F[Car] =
    SQLQueries.updateCar(car).transact(transactor)
  def deleteCar(id: Long): F[Unit] =
    SQLQueries.deleteCar(id).map(_ => ()).transact(transactor)
  def deleteCars: F[Unit] =
    SQLQueries.deleteAllCars.map(_ => ()).transact(transactor)

  object SQLQueries {
    private def selectFragment: Fragment =
      fr"""SELECT * FROM car"""
    private def deleteFragment: Fragment =
      fr"""DELETE FROM car"""
    private def carIdFilter(id: Long): Fragment =
      fr""" WHERE car_id = $id """
    def allCars: ConnectionIO[List[Car]] =
      selectFragment.query[Car].to[List]

    def getCar(id: Long): ConnectionIO[Option[Car]] =
      (selectFragment ++ carIdFilter(id)).query[Car].option

    def insertCar(car: Car): ConnectionIO[Car] =
      sql"""
            INSERT INTO car
              (licence_plate, color, navn)
            VALUES
              (
                ${car.licenseNumber},
                ${car.color},
                ${car.name}
                )
      """.update.withUniqueGeneratedKeys[Car]("*")

    def updateCar(car: Car): ConnectionIO[Car] =
      (fr"""
            UPDATE
              car
            SET
              licence_plate = ${car.licenseNumber},
              color = ${car.color},
              navn = ${car.name}"""
        ++ carIdFilter(car.id)).update.withUniqueGeneratedKeys[Car]("*")

    def deleteCar(id: Long): ConnectionIO[Int] =
      (deleteFragment ++ carIdFilter(id)).update.run

    def deleteAllCars: ConnectionIO[Int] =
      deleteFragment.update.run
  }
}
