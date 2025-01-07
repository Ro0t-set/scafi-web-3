val scafiVersion = "1.3.0"
scalafmtOnCompile := true
wartremoverErrors ++= Warts.unsafe
wartremoverErrors --= Seq(
  Wart.DefaultArguments,
)

enablePlugins(CucumberPlugin)

CucumberPlugin.glues := List("features")
CucumberPlugin.features := List("src/test/resources/features")
// settings for cucumber 6 - see https://github.com/sbt/sbt-cucumber/issues/15
CucumberPlugin.mainClass := "io.cucumber.core.cli.Main"
CucumberPlugin.plugin := {
  import com.waioeka.sbt.Plugin._
  val cucumberDir = CucumberPlugin.cucumberTestReports.value
  List(
    HtmlPlugin(new File(cucumberDir, "/resources/cucumber.html")),
  )
}

lazy val scafiWeb3 = project.in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
    scalaVersion                    := "3.3.4",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withOptimizer(false)
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



