import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"

  def testDeps = Seq(scalaTest).map(_ % Test)
}
