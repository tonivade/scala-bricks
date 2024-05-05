enablePlugins(JlinkPlugin)

name := "scala-bricks"

version := "1.1.0"

scalaVersion := "2.13.14"

libraryDependencies += "org.openjfx" % "javafx-base" % "22.0.1"
libraryDependencies += "org.openjfx" % "javafx-controls" % "22.0.1"
libraryDependencies += "org.openjfx" % "javafx-fxml" % "22.0.1"
libraryDependencies += "org.openjfx" % "javafx-graphics" % "22.0.1"
libraryDependencies += "org.openjfx" % "javafx-media" % "22.0.1"

libraryDependencies += "org.scalafx" %% "scalafx" % "22.0.0-R33"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % "test"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.10.0"
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
