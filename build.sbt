name := "scala-bricks"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.8"

libraryDependencies += "org.openjfx" % "javafx-base" % "17.0.2"
libraryDependencies += "org.openjfx" % "javafx-controls" % "17.0.2"
libraryDependencies += "org.openjfx" % "javafx-fxml" % "17.0.2"
libraryDependencies += "org.openjfx" % "javafx-graphics" % "17.0.2"
libraryDependencies += "org.openjfx" % "javafx-media" % "17.0.2"

libraryDependencies += "org.scalafx" %% "scalafx" % "17.0.1-R26"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % "test"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.7.0"
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.5.4"

/*
Taken from https://github.com/kubukoz/steve
Create reflect-config.json and resource-config.json based on by running nativeImageAgentOutputDir :
https://github.com/scalameta/sbt-native-image#nativeimagerunagent
 */
lazy val nativeImage =
  project
    .in(file("."))
    .enablePlugins(NativeImagePlugin)
    .settings(
      Compile / mainClass := Some("tomby.scala.bricks.ClickEmAll"),
      nativeImageOptions ++= Seq(
        "--no-fallback",
        s"-H:ReflectionConfigurationFiles=${(Compile / resourceDirectory).value / "reflect-config.json"}",
        s"-H:ResourceConfigurationFiles=${(Compile / resourceDirectory).value / "resource-config.json"}",
        s"-H:JNIConfigurationFiles=${(Compile / resourceDirectory).value / "jni-config.json"}",
        "--allow-incomplete-classpath",
      ),
      nativeImageVersion := "22.0.0.2",
      nativeImageJvm := "graalvm-java17",
      nativeImageAgentMerge := true
    )

fork := true
