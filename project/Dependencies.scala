import sbt._

object Dependencies {
  private val http4sVersion = "0.21.24"
  private val doobieVersion = "0.13.4"
  private val circeVersion  = "0.14.1"
  private val catsVersion   = "2.6.1"

  val cats = Seq(
    "org.typelevel" %% "cats-core",
    "org.typelevel" %% "cats-kernel"
  ).map(_ % catsVersion)

  val http4s = Seq(
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-blaze-server",
    "org.http4s" %% "http4s-circe"
  ).map(_ % http4sVersion)

  val json = Seq(
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-literal"
  ).map(_ % circeVersion)

  val doobie = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-postgres"
  ).map(_ % doobieVersion)

  val database = Seq(
    "io.zonky.test"  % "embedded-postgres" % "1.3.0",
    "org.flywaydb"   % "flyway-core"       % "7.10.0",
    "org.postgresql" % "postgresql"        % "42.2.22"
  ) ++ doobie

  val divDeps = Seq(
    "org.slf4j" % "slf4j-simple" % "1.7.31"
  )

  val testDeps = Seq(
    "org.specs2" %% "specs2-core"       % "4.12.1",
    "org.specs2" %% "specs2-scalacheck" % "4.12.1",
    "org.specs2" %% "specs2-mock"       % "4.12.1"
  ).map(_ % Test)
  val coreDeps = cats ++ http4s ++ database ++ divDeps ++ json
}
