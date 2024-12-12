import org.scalajs.linker.interface.ModuleSplitStyle


val scafiVersion = "1.3.0"


lazy val livechart = project.in(file("."))
  .enablePlugins(ScalaJSPlugin) // Enable the Scala.js plugin in this project
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
      scalaVersion := "3.5.2",


      // Tell Scala.js that this is an application with a main method
      scalaJSUseMainModuleInitializer := true,

      /* Configure Scala.js to emit modules in the optimal way to
       * connect to Vite's incremental reload.
       * - emit ECMAScript modules
       * - emit as many small modules as possible for classes in the "livechart" package
       * - emit as few (large) modules as possible for all other classes
       *   (in particular, for the standard library)
       */
      scalaJSLinkerConfig ~= {
          _.withModuleKind(ModuleKind.ESModule)
            .withModuleSplitStyle(
                ModuleSplitStyle.SmallModulesFor(List("livechart")))
      },

      /* Depend on the scalajs-dom library.
       * It provides static types for the browser DOM APIs.
       */

      libraryDependencies ++= Seq(
        ("it.unibo.scafi" %%% "scafi-core" % scafiVersion).cross(CrossVersion.for3Use2_13),
        ("it.unibo.scafi" %%% "scafi-commons" % scafiVersion).cross(CrossVersion.for3Use2_13),
        ("it.unibo.scafi" %%% "scafi-simulator" % scafiVersion).cross(CrossVersion.for3Use2_13)
      ),
      libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",

      // Depend on Laminar
      libraryDependencies += "com.raquo" %%% "laminar" % "17.0.0",

      // Testing framework
      libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0" % Test,

      // Tell ScalablyTyped that we manage `npm install` ourselves
      externalNpm := baseDirectory.value,
  )
