enablePlugins(JlinkPlugin)

name := "scala-bricks"

version := "1.2.0"

scalaVersion := "2.13.16"

libraryDependencies += "org.openjfx" % "javafx-base" % "24.0.1"
libraryDependencies += "org.openjfx" % "javafx-controls" % "24.0.1"
libraryDependencies += "org.openjfx" % "javafx-fxml" % "24.0.1"
libraryDependencies += "org.openjfx" % "javafx-graphics" % "24.0.1"
libraryDependencies += "org.openjfx" % "javafx-media" % "24.0.1"

libraryDependencies += "org.scalafx" %% "scalafx" % "24.0.0-R35"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.13.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.5.5"

fork := true

scalacOptions += "-deprecation"

Compile / mainClass := Some("tomby.scala.bricks.ClickEmAll")

jlinkIgnoreMissingDependency := JlinkIgnore.only(
  "scalafx" -> "javafx.embed.swing",
  "scalafx" -> "javafx.scene.web",
  "scalafx.embed.swing" -> "javafx.embed.swing",
  "scalafx.scene.web" -> "javafx.scene.web"
)

jlinkModules += "jdk.crypto.ec"

maintainer := "antoniogmc@gmail.com"
