lazy val akkaVersion = "2.5.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
//  "io.kamon" % "sigar-loader" % "1.6.6-rev002",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",

  // https://mvnrepository.com/artifact/org.codehaus.groovy/groovy-all
  "org.codehaus.groovy" % "groovy-all" % "2.4.12",
  // https://mvnrepository.com/artifact/joda-time/joda-time
  "joda-time" % "joda-time" % "2.9.9",
  // https://mvnrepository.com/artifact/com.google.guava/guava
  "com.google.guava" % "guava" % "23.0",
  // https://mvnrepository.com/artifact/commons-io/commons-io
  "commons-io" % "commons-io" % "2.5"
)