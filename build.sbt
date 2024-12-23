val scafiVersion = "1.3.0"
scalafmtOnCompile := true
lazy val scafiWeb3 = project.in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
    scalaVersion                    := "3.3.4",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withOptimizer(false)
    }
    ,
    libraryDependencies ++= Seq(
      ("it.unibo.scafi" %%% "scafi-core" % scafiVersion).cross(
        CrossVersion.for3Use2_13
      ),
      ("it.unibo.scafi" %%% "scafi-commons" % scafiVersion).cross(
        CrossVersion.for3Use2_13
      ),
      ("it.unibo.scafi" %%% "scafi-simulator" % scafiVersion).cross(
        CrossVersion.for3Use2_13
      )
    ),
    libraryDependencies += "org.scala-js"  %%% "scalajs-dom" % "2.8.0",
    libraryDependencies += "com.raquo"     %%% "laminar"     % "17.2.0",
    libraryDependencies += "org.scalameta" %%% "munit"       % "1.0.0" % Test,
    externalNpm                             := baseDirectory.value
  )
