import sbt.Keys.javaOptions

lazy val root = project.in(file("."))
  .aggregate(scafiWeb3, scafiWeb3Cucumber)
  .enablePlugins(ScalafixPlugin)
  .enablePlugins(ScalafmtPlugin)
  .enablePlugins(WartRemover)
  .settings(
    name := "scafi-web-3-root",
    ThisBuild / scalafmtOnCompile := true,
    ThisBuild / wartremoverErrors ++= Warts.unsafe,
    ThisBuild / wartremoverErrors --= Seq(Wart.DefaultArguments),
    ThisBuild / semanticdbEnabled := true,
    ThisBuild / semanticdbVersion := scalafixSemanticdb.revision,
    ThisBuild / scalacOptions ++= List("-Wunused:all"),
    ThisBuild / scalafixOnCompile := true,
    ThisBuild / scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % "0.5.1",
    ThisBuild / evictionErrorLevel := Level.Info
  )

(Test / scalacOptions) += "-scalajs-genStaticForwardersForNonTopLevelObjects"

lazy val scafiWeb3 = project.in(file("js"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
    scalaVersion := "3.3.4",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    externalNpm := baseDirectory.value,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.8.0",
      "com.raquo" %%% "laminar" % "17.2.0",
      "com.lihaoyi" %%% "upickle" % "4.1.0",
      "org.scalameta" %%% "munit" % "1.1.0" % Test,
      "org.scalameta" %%% "munit-scalacheck" % "1.1.0" % Test,
    )
  )

lazy val scafiWeb3Cucumber = project.in(file("cucumber"))
  .enablePlugins(CucumberPlugin)
  .settings(
    scalaVersion := "3.3.4",
    libraryDependencies ++= Seq(
      "io.cucumber" %% "cucumber-scala" % "8.25.1" % Test,
      "io.cucumber" % "cucumber-junit" % "7.20.1" % Test,
      "org.scalatestplus" %% "selenium-4-21" % "3.2.19.0" % Test,
    ),
    CucumberPlugin.glues := List("features"),
    CucumberPlugin.features := List("cucumber/src/test/resources/features"),
    CucumberPlugin.mainClass := "io.cucumber.core.cli.Main",
    CucumberPlugin.javaOptions := Seq(
      s"-DtestEnv=${System.getProperty("testEnv", "ci")}",
      s"-Dbrowser=${System.getProperty("browser", "firefox")}"
    ),
    CucumberPlugin.plugin := {
      import com.waioeka.sbt.Plugin._
      val cucumberDir = CucumberPlugin.cucumberTestReports.value
      println(cucumberDir)
      List(HtmlPlugin(new File(cucumberDir, "cucumber.html")))
    }
  )


ThisBuild / evictionErrorLevel := Level.Info

lazy val startNpmServer = taskKey[Unit]("Start the npm server on port 8080")

startNpmServer := {
  val process = sys.process.Process("npm run dev").run()
  Thread.sleep(15000)
  println("Server npm running!")
}


lazy val cucumberWithServer = taskKey[Unit]("Run Cucumber tests with npm server")
cucumberWithServer := {
  startNpmServer.value
  try {
    val result = sys.process.Process("sbt cucumber").!
    if (result == 0) println("Cucumber tests passed!")
    else println("Cucumber tests failed...")
  } finally {
    val result = sys.process.Process("pkill -f \"npm run dev\"").!
    if (result == 0) println("Server npm stopped!")
    else println("Server non found...")
  }
}
