import sbt.Keys.javaOptions
scalafmtOnCompile := true
wartremoverErrors ++= Warts.unsafe
wartremoverErrors --= Seq(
  Wart.DefaultArguments
)

(Test / scalacOptions) += "-scalajs-genStaticForwardersForNonTopLevelObjects"

enablePlugins(ScalafixPlugin)
ThisBuild / semanticdbEnabled          := true
ThisBuild / semanticdbVersion          := scalafixSemanticdb.revision
ThisBuild / scalacOptions  ++= List("-Wunused:all")
ThisBuild / scalafixOnCompile := true
ThisBuild / scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % "0.5.1"


enablePlugins(CucumberPlugin)

CucumberPlugin.glues := List("features")
CucumberPlugin.features := List("src/test/resources/features")
CucumberPlugin.mainClass := "io.cucumber.core.cli.Main"
CucumberPlugin.javaOptions := Seq(
  s"-DtestEnv=${System.getProperty("testEnv", "ci")}",
          s"-Dbrowser=${System.getProperty("browser", "firefox")}"
)
CucumberPlugin.plugin := {
  import com.waioeka.sbt.Plugin._
  val cucumberDir = CucumberPlugin.cucumberTestReports.value
  println(cucumberDir)
  List(
    HtmlPlugin(new File(cucumberDir, "cucumber.html"))
  )
}


lazy val scafiWeb3 = project.in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
    scalaVersion                    := "3.3.4",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withBatchMode(false) },
    scalaJSLinkerConfig ~= {

      _.withModuleKind(ModuleKind.ESModule)
        .withOptimizer(false)
        .withClosureCompiler(false)
        .withSourceMap(true)
    },

    libraryDependencies += "org.scala-js"  %%% "scalajs-dom" % "2.8.0",
    libraryDependencies += "com.raquo"     %%% "laminar"     % "17.2.0",
    libraryDependencies += "org.scalameta" %%% "munit"       % "1.0.3" % Test,
    libraryDependencies += "io.cucumber" %% "cucumber-scala" % "8.25.1" % Test,
    libraryDependencies += "io.cucumber" % "cucumber-junit" % "7.20.1" % Test,
    libraryDependencies += "org.scalatestplus" %% "selenium-4-21" % "3.2.19.0" % Test,
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "4.0.2",
    externalNpm                             := baseDirectory.value
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
