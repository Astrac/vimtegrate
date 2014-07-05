import sbt._
import Keys._

object VimtegrateBuild extends Build {
  lazy val vimtegrateProject = Project(id = "vimtegrate", base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "vimtegrate",
      version := "0.1",
      scalaVersion := "2.10.3",
      sbtPlugin := true,
      organization := "astrac",
      scalacOptions ++= Seq("-deprecation", "-feature")
    ))
}
