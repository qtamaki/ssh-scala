name := """ssh-scala"""

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.8"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.hierynomus" % "sshj" % "0.16.0",
  "org.slf4j" % "slf4j-api" % "1.7.7",
  "org.bouncycastle" % "bcprov-jdk15on" % "1.51",
  "org.bouncycastle" % "bcpkix-jdk15on" % "1.51",
  "com.jcraft" % "jzlib" % "1.1.3",
  "net.vrallev.ecc" % "ecc-25519-java" % "1.0.1"
)

