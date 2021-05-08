name := "scala-bricks"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.5"

val osName: SettingKey[String] = SettingKey[String]("osName")

osName := (System.getProperty("os.name") match {
  case name if name.startsWith("Linux") => "linux"
  case name if name.startsWith("Mac") => "mac"
  case name if name.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
})

libraryDependencies += "org.openjfx" % "javafx-base" % "16" classifier osName.value
libraryDependencies += "org.openjfx" % "javafx-controls" % "16" classifier osName.value
libraryDependencies += "org.openjfx" % "javafx-fxml" % "16" classifier osName.value
libraryDependencies += "org.openjfx" % "javafx-graphics" % "16" classifier osName.value
libraryDependencies += "org.openjfx" % "javafx-media" % "16" classifier osName.value

libraryDependencies += "org.scalafx" %% "scalafx" % "16.0.0-R22"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % "test"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.6.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.5.0"
