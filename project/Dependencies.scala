import sbt._

object Dependencies {
  private val http4sVersion = "0.18.0-M8"
  private val doobieVersion = "0.5.0-M13"
  private val circeVersion  = "0.9.0"
  private val catsVersion   = "1.0.1"

  lazy val scalaTest = "org.scalatest" %% "scalatest"   % "3.0.3"
  val logging        = "org.slf4j"     % "slf4j-simple" % "1.7.25"

  val cats = Seq(
    "org.typelevel" %% "cats-core",
    "org.typelevel" %% "cats-macros",
    "org.typelevel" %% "cats-kernel"
  ).map(_ % catsVersion)

  val mouse = "org.typelevel" %% "mouse" % "0.16"

  val http4s = Seq(
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-blaze-server",
    "org.http4s" %% "http4s-circe",
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
    "com.opentable.components" % "otj-pg-embedded" % "0.10.0",
    "org.flywaydb"             % "flyway-core"     % "5.0.5"
  ) ++ doobie

  val divDeps = Seq(
    logging,
    mouse
  )

  val testDeps = Seq(scalaTest).map(_ % Test)
  val coreDeps = cats ++ http4s ++ database ++ divDeps ++ json
}
