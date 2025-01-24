addSbtPlugin("org.scala-js"                % "sbt-scalajs"   % "1.18.2")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta44")
addSbtPlugin("org.scalameta"               % "sbt-scalafmt"  % "2.5.2")
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.2.5")
addSbtPlugin("com.github.sbt" % "sbt-unidoc" % "0.5.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.3.2")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.14.0")
addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.2.10")
addSbtPlugin("com.waioeka.sbt" % "cucumber-plugin" % "0.3.1")
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always