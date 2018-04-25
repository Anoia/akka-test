name := "akka-test"

version := "0.1"

scalaVersion := "2.12.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.12",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.12" % Test,
  "com.typesafe.akka" %% "akka-http" % "10.1.1",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.1",
  "org.json4s"          %%  "json4s-jackson"          % "3.5.0"   withSources() withJavadoc(),
  "org.json4s"          %%  "json4s-ext"              % "3.5.0"   withSources() withJavadoc()
)
