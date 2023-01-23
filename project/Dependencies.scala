import sbt._

object Dependencies {
  private val http4sVersion = "0.23.18"
  private val doobieVersion = "1.0.0-RC2"
  private val circeVersion  = "0.14.3"
  private val catsVersion   = "2.9.0"

  val coreDeps = Seq(
    "org.typelevel"              %% "cats-core"                  % catsVersion,
    "org.http4s"                 %% "http4s-dsl"                 % http4sVersion,
    "org.http4s"                 %% "http4s-ember-server"        % http4sVersion,
    "org.http4s"                 %% "http4s-circe"               % http4sVersion,
    "io.circe"                   %% "circe-generic"              % circeVersion,
    "io.circe"                   %% "circe-literal"              % circeVersion,
    "org.tpolecat"               %% "doobie-core"                % doobieVersion,
    "org.tpolecat"               %% "doobie-postgres"            % doobieVersion,
    "io.zonky.test"               % "embedded-postgres"          % "2.0.2",
    "org.flywaydb"                % "flyway-core"                % "9.12.0",
    "org.slf4j"                   % "slf4j-api"                  % "2.0.6",
    "ch.qos.logback"              % "logback-core"               % "1.4.5",
    "ch.qos.logback"              % "logback-classic"            % "1.4.5",
    "com.typesafe.scala-logging" %% "scala-logging"              % "3.9.5",
    "org.specs2"                 %% "specs2-core"                % "4.19.0" % Test,
    "org.typelevel"              %% "cats-effect-testing-specs2" % "1.5.0"  % Test
  )
}
