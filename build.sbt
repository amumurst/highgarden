import Dependencies._

lazy val commonSettings = Seq(
  organization := "no.amumurst",
  scalaVersion := "2.12.4",
  version      := "0.1.0-SNAPSHOT",
  scalacOptions ++= Seq("-Ypartial-unification"),
  libraryDependencies ++= testDeps ++ coreDeps,
  addCompilerPlugin(
    "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
  )
)

lazy val lib = (project in file("lib")).settings(commonSettings)

lazy val server = (project in file("server")).dependsOn(lib).settings(commonSettings)

lazy val root = (project in file("."))
  .aggregate(lib, server)