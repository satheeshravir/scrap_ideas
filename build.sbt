name := "scrap_ideas"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= {
  val akkaV       = "2.5.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor"                           % akkaV,
    "org.jsoup"         % "jsoup"                                 % "1.8+",
    "commons-validator" % "commons-validator"                     % "1.5+",
    "com.typesafe.play" % "play-json_2.12"                        % "2.6.0-M7"

  )
}