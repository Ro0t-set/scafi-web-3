import org.scalajs.linker.interface.ModuleSplitStyle

val scafiVersion = "1.3.0"
scalafmtOnCompile := true

lazy val branch = "master"

lazy val scafiWeb3 = project.in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
    scalaVersion                    := "3.6.2",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("ScafiWeb3"))
        )
    },

    credentials += Credentials(Path.userHome / ".sbt" / ".credentials"),
    resolvers += "jitpack" at "https://jitpack.io",
    libraryDependencies += "com.github.field4s.field4s" % "core_native0.5_3" % "1.0.0",

      libraryDependencies += "org.scala-js"  %%% "scalajs-dom" % "2.8.0",
    libraryDependencies += "com.raquo"     %%% "laminar"     % "17.2.0",
    libraryDependencies += "org.scalameta" %%% "munit"       % "1.0.0" % Test,
    externalNpm                             := baseDirectory.value
  )
