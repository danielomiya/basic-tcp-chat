import sbt._

object Dependencies {
  // versions
  lazy val scalatestVersion = "3.2.9"

  // libs
  val scalactic = "org.scalactic" %% "scalactic" % scalatestVersion
  val scalatest = "org.scalatest" %% "scalatest" % scalatestVersion
  val argonaut = "io.argonaut" %% "argonaut" % "6.2.2"

  // projects
  val sharedDeps = Seq(
    argonaut,
  )

  val serverDeps = Seq(
    scalactic % Test,
    scalatest % Test,
  )

  val clientDeps = Seq(
    scalactic % Test,
    scalatest % Test,
  )
}
