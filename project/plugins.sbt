addSbtPlugin("org.scala-js"                % "sbt-scalajs"   % "1.18.1")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta44")
addSbtPlugin("org.scalameta"               % "sbt-scalafmt"  % "2.5.2")
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.2.5")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.13.0")
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.2.8")
addSbtPlugin("com.waioeka.sbt" % "cucumber-plugin" % "0.3.1")
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always