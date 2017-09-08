name := "dwarf-akka"

organization in ThisBuild := "com.derby.nuke"

version in ThisBuild := "1.0"

scalaVersion in ThisBuild := "2.12.2"

//lazy val root = Project(
//  id = "root",
//  base = file("."),
//  // always run all commands on each sub project
//  aggregate = Seq(api, server)
//) dependsOn(api, server) // this does the actual aggregation

lazy val api = Project(
  id = "api",
  base = file("modules/api")
)

lazy val server = Project(
  id = "server",
  base = file("modules/server"),
  aggregate = Seq(api)
) dependsOn (api)