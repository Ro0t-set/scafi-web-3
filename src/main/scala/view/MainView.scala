package view

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import state.AnimationState.{engine, running}
import state.GraphState.{edges, nodes}
import view.components.{
  AnimationControllerView,
  EngineSettingsView,
  GridViewControllerView
}
import view.player.EngineLoopPlayer as EngineControllerPlayer
import view.config.ViewConfig
import view.controller.EngineController
import view.graph.scene.ThreeSceneImpl

final class MainView(config: ViewConfig):
  private val scene               = ThreeSceneImpl(config.sceneConfig)
  private val sceneController     = GridViewControllerView(scene)
  private val engineController    = EngineController()()()
  private val engineSettings      = EngineSettingsView(engineController)
  private val animationController = AnimationControllerView()
  private val player              = EngineControllerPlayer

  private def initialize(): Unit =
    ClientMain.signal
    def newScastieLoadingSignal(
        result: scala.scalajs.js.Any,
        attachedElements: scala.scalajs.js.Any,
        scastieId: scala.scalajs.js.Any
    ): Unit =
      engineController.loadEngine()
      scene.centerView()
    scala.scalajs.js.Dynamic.global.scastie.ClientMain.signal =
      newScastieLoadingSignal

  def render(): Unit =
    val rootElement = div(
      scene.renderScene("three_canvas"),
      sceneController.render,
      animationController.render,
      engineSettings.render,
      running --> {
        case true => player.start()
        case _    => ()
      },
      engine --> {
        case Some(engine) => player.loadNextFrame()
        case _            => ()
      },
      edges.combineWith(nodes) --> {
        case (edges, nodes) =>
          scene.setNodes(nodes)
          scene.setEdges(edges)
      },
      onMountCallback(_ => initialize())
    )

    renderOnDomContentLoaded(
      dom.document.getElementById("app"),
      rootElement
    )
