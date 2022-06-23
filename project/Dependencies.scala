import sbt._

object Dependencies {
  private val http4sVersion = "0.23.12"
  private val doobieVersion = "1.0.0-RC1"
  private val circeVersion  = "0.14.2"
  private val catsVersion   = "2.8.0"

  val cats = Seq(
    "org.typelevel" %% "cats-core"
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
    "io.zonky.test"  % "embedded-postgres" % "1.3.1",
    "org.flywaydb"   % "flyway-core"       % "8.5.13",
    "org.postgresql" % "postgresql"        % "42.4.0"
  ) ++ doobie

  val divDeps = Seq(
    "org.slf4j"                   % "slf4j-api"       % "1.7.36",
    "ch.qos.logback"              % "logback-core"    % "1.2.11",
    "ch.qos.logback"              % "logback-classic" % "1.2.11",
    "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.5"
  )

  val testDeps = Seq(
    "org.specs2"    %% "specs2-core"                % "4.16.0",
    "org.typelevel" %% "cats-effect-testing-specs2" % "1.4.0"
  ).map(_ % Test)
  val coreDeps = cats ++ http4s ++ database ++ divDeps ++ json
}
