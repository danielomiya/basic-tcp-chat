import Dependencies._

ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.12.14"

lazy val shared = (project in file("shared"))
  .settings(
    name := "chat-shared",
    libraryDependencies ++= sharedDeps,
    idePackagePrefix := Some("com.github.gwyddie.chat.shared"),
  )

lazy val server = (project in file("server"))
  .dependsOn(shared)
  .settings(
    name := "chat-server",
    libraryDependencies ++= serverDeps,
    idePackagePrefix := Some("com.github.gwyddie.chat.server"),
  )

lazy val client = (project in file("client"))
  .dependsOn(shared)
  .settings(
    name := "chat-client",
    libraryDependencies ++= clientDeps,
    idePackagePrefix := Some("com.github.gwyddie.chat.client"),
  )
