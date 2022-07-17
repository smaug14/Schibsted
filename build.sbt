ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "Schibsted",
    assembly / mainClass := Some("boot.Main"),
    assembly / assemblyJarName := "schibsted-search.jar"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % "test"
