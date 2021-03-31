ThisBuild / scalaVersion := "2.13.4"
ThisBuild / organization := "liamseymour"

lazy val hello = (project in file("."))
  .settings(
    name := "Classify"
  )

