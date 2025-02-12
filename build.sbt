ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.3"

lazy val root = (project in file("."))
  .settings(
    name := "raylib-ui",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
  )
