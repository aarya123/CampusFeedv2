name := "server"

version := "1.0-SNAPSHOT"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.26"


libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)


play.Project.playJavaSettings
