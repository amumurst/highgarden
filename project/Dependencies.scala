import sbt._

object Dependencies {
  private val http4sVersion = "0.18.0-M8"
  private val doobieVersion = "0.5.0-M12"
  private val circeVersion  = "0.9.0"

  lazy val scalaTest = "org.scalatest" %% "scalatest"   % "3.0.3"
  val logging        = "org.slf4j"     % "slf4j-simple" % "1.7.25"

  val http4s = Seq(
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-circe"        % http4sVersion,
  )

  val database = Seq(
    "com.opentable.components" % "otj-pg-embedded"  % "0.10.0",
    "org.flywaydb"             % "flyway-core"      % "5.0.5",
    "org.tpolecat"             %% "doobie-core"     % doobieVersion,
    "org.tpolecat"             %% "doobie-postgres" % doobieVersion
  )
  val json = Seq(
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-literal"
  ).map(_ % circeVersion)

  val divDeps = Seq(
    logging
  )

  val testDeps = Seq(scalaTest).map(_ % Test)
  val coreDeps = http4s ++ database ++ divDeps ++ json
}
