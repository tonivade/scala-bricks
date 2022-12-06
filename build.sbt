name := "scala-bricks"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.10"

// Determine OS version of JavaFX binaries
lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new UnsupportedOperationException("Unknown platform!")
}

libraryDependencies += "org.openjfx" % "javafx-base" % "17.0.2"
libraryDependencies += "org.openjfx" % "javafx-controls" % "17.0.2"
libraryDependencies += "org.openjfx" % "javafx-fxml" % "17.0.2"
libraryDependencies += "org.openjfx" % "javafx-graphics" % "17.0.2"
libraryDependencies += "org.openjfx" % "javafx-media" % "17.0.2"

libraryDependencies += "org.scalafx" %% "scalafx" % "17.0.1-R26"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.9.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.5.5"

lazy val nativeImage =
  project
    .in(file("."))
    .enablePlugins(NativeImagePlugin)
    .settings(
      Compile / mainClass := Some("tomby.scala.bricks.ClickEmAll"),
      nativeImageOptions ++= Seq(
        "--no-fallback",
        s"-H:ReflectionConfigurationFiles=${(Compile / resourceDirectory).value / osName / "reflect-config.json"}",
        s"-H:ResourceConfigurationFiles=${(Compile / resourceDirectory).value / osName / "resource-config.json"}",
        s"-H:JNIConfigurationFiles=${(Compile / resourceDirectory).value / osName / "jni-config.json"}",
        "--allow-incomplete-classpath",
      ),
      nativeImageVersion := "22.0.0.2",
      nativeImageJvm := "graalvm-java17",
      nativeImageAgentMerge := true
    )

fork := true
