import Dependencies._

enablePlugins(DockerPlugin)

val dockerSettings = Seq(
  dockerfile in docker := {
    val artifact: File = assembly.value
    val artifactTargetPath = s"/app/${artifact.name}"

    new Dockerfile {
      from("java")
      run("apt-get", "update")
      run("apt-get", "-y", "install", "postgresql")
      user("postgres")
      expose(8080)
      add(artifact, artifactTargetPath)
      entryPoint("java", "-jar", artifactTargetPath)
    }
  },
  imageNames in docker := Seq(
    ImageName(s"amumurst/${name.value}:latest")
  )
)

lazy val baseSettings = Seq(
  organization := "no.amumurst",
  scalaVersion := "2.12.4",
  version      := "0.1.0-SNAPSHOT"
)

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-Ypartial-unification",
    "-feature",
    "-language:higherKinds",
  ),
  libraryDependencies ++= testDeps ++ coreDeps,
  addCompilerPlugin(
    "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
  ),
  wartremoverErrors ++=
    Warts.allBut(
      Wart.DefaultArguments,
      Wart.FinalCaseClass,
      Wart.ImplicitParameter,
      Wart.Overloading,
      Wart.ToString,
      Wart.PublicInference),
  test in assembly := {}
) ++ baseSettings

lazy val lib = (project in file("lib")).settings(commonSettings)

lazy val server = (project in file("server")).dependsOn(lib).settings(commonSettings)

lazy val root =
  (project in file("."))
    .dependsOn(server)
    .aggregate(lib, server)
    .settings(
      baseSettings,
      name := "highgarden",
      mainClass.in(Compile) := mainClass.in(Compile).in(server).value,
      dockerSettings)