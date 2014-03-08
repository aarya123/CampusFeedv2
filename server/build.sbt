name := "server"

version := "1.0-SNAPSHOT"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.26"

libraryDependencies ++= Seq(
  javaJdbc,
   "org.json"%"org.json"%"chargebee-1.0",
  javaEbean,
  cache
)


play.Project.playJavaSettings
