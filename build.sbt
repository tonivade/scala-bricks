enablePlugins(JlinkPlugin)

name := "scala-bricks"

version := "1.1.0"

scalaVersion := "2.13.12"

// Determine OS version of JavaFX binaries
lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new UnsupportedOperationException("Unknown platform!")
}

libraryDependencies += "org.openjfx" % "javafx-base" % "21.0.1"
libraryDependencies += "org.openjfx" % "javafx-controls" % "21.0.1"
libraryDependencies += "org.openjfx" % "javafx-fxml" % "21.0.1"
libraryDependencies += "org.openjfx" % "javafx-graphics" % "21.0.1"
libraryDependencies += "org.openjfx" % "javafx-media" % "21.0.1"

libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % "test"
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