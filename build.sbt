ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.3"

lazy val root = (project in file("."))
  .enablePlugins(ScalaTsiPlugin)
  .settings(
    name := "raylib-ui",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
    libraryDependencies += "dev.zio" %% "zio-json" % "0.7.28",
    typescriptOutputFile := sourceDirectory.value / "main" / "typescript" / "model.ts",
    typescriptExports := Seq("Batch"),
    typescriptGenerationImports := Seq("draw.serial.*, DrawTSTypes.given"),
  )
