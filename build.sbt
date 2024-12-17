import org.scalajs.linker.interface.ModuleSplitStyle


val scafiVersion = "1.3.0"

lazy val scafiWeb3 = project.in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
    scalaVersion := "3.5.2",


    scalaJSUseMainModuleInitializer := true,

    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("ScafiWeb3")))
    },

    libraryDependencies ++= Seq(
      ("it.unibo.scafi" %%% "scafi-core" % scafiVersion).cross(CrossVersion.for3Use2_13),
      ("it.unibo.scafi" %%% "scafi-commons" % scafiVersion).cross(CrossVersion.for3Use2_13),
      ("it.unibo.scafi" %%% "scafi-simulator" % scafiVersion).cross(CrossVersion.for3Use2_13)
    ),
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",
    libraryDependencies += "com.raquo" %%% "laminar" % "17.0.0",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0" % Test,

    externalNpm := baseDirectory.value,
  )