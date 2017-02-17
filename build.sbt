name := "scala-bricks"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.1"

jfxSettings

JFX.artifactBaseNameValue <<= name

JFX.mainClass := Some("tomby.scala.bricks.BoardGUI")
