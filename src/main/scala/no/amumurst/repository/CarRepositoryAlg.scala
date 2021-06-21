package no.amumurst
package repository

import cats.effect._
import doobie._
import doobie.implicits._
import no.amumurst.domain.Car

trait CarRepositoryAlg[F[_]] {
  def getAllCars: F[List[Car]]
  def getCar(id: Long): F[Option[Car]]
  def insertCar(car: Car): F[Either[Throwable, Car]]
  def updateCar(car: Car): F[Either[Throwable, Car]]
  def deleteCar(id: Long): F[Unit]
  def deleteCars: F[Unit]
}

case class CarRepository(transactor: Transactor[IO]) extends CarRepositoryAlg[IO] {

  val getAllCars: IO[List[Car]] =
    CarRepositoryQueries.allCars.transact(transactor)
  def getCar(id: Long): IO[Option[Car]] =
    CarRepositoryQueries.getCar(id).transact(transactor)
  def insertCar(car: Car): IO[Either[Throwable, Car]] =
    CarRepositoryQueries.insertCar(car).transact(transactor).attempt
  def updateCar(car: Car): IO[Either[Throwable, Car]] =
    CarRepositoryQueries.updateCar(car).transact(transactor).attempt
  def deleteCar(id: Long): IO[Unit] =
    CarRepositoryQueries.deleteCar(id).map(_ => ()).transact(transactor)
  val deleteCars: IO[Unit] =
    CarRepositoryQueries.deleteAllCars.map(_ => ()).transact(transactor)
}

object CarRepositoryQueries {
  private val selectFragment: Fragment =
    fr"""SELECT * FROM car"""
  private val deleteFragment: Fragment =
    fr"""DELETE FROM car"""
  private def carIdFilter(id: Long): Fragment =
    fr""" WHERE car_id = $id """

  val allCars: ConnectionIO[List[Car]] =
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

  val deleteAllCars: ConnectionIO[Int] =
    deleteFragment.update.run

}
