import Dependencies._

enablePlugins(DockerPlugin)

val dockerSettings = Seq(
  docker / dockerfile := {
    val artifact: File     = assembly.value
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
  docker / imageNames := Seq(
    ImageName(s"amumurst/${name.value}:latest")
  )
)

lazy val baseSettings = Seq(
  organization := "no.amumurst",
  scalaVersion := "2.13.6",
  version := "0.1.0-SNAPSHOT"
)

lazy val commonSettings = Seq(
  libraryDependencies ++= testDeps ++ coreDeps,
  assembly / test := {},
  Test / fork := true
) ++ baseSettings

lazy val root =
  (project in file("."))
    .settings(commonSettings, name := "highgarden", dockerSettings)
