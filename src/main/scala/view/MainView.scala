package view

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import state.GraphState.{edges, nodes}
import view.components.{
  AnimationControllerView,
  EngineSettingsView,
  GridViewControllerView
}
import view.player.EngineController as EngineControllerPlayer
import view.config.ViewConfig
import view.controller.EngineController
import view.graph.scene.ThreeSceneImpl

final class MainView(config: ViewConfig):
  private val scene               = ThreeSceneImpl(config.sceneConfig)
  private val sceneController     = new GridViewControllerView(scene)
  private val engineController    = new EngineController()()()
  private val engineSettings      = new EngineSettingsView(engineController)
  private val animationController = new AnimationControllerView

  private def initialize(): Unit =
    val originalSignal = ClientMain.signal
    def newSignal(
        result: scala.scalajs.js.Any,
        attachedElements: scala.scalajs.js.Any,
        scastieId: scala.scalajs.js.Any
    ): Unit =
      engineController.loadEngine()
      scene.centerView()

    scala.scalajs.js.Dynamic.global.scastie.ClientMain.signal = newSignal

  def render(): Unit =
    val rootElement = div(
      scene.renderScene("three_canvas"),
      sceneController.render,
      animationController.render,
      engineSettings.render,
      onMountCallback { ctx =>
        initialize()
        EngineControllerPlayer.Player(ctx.owner)
        edges.combineWith(nodes).foreach { case (e, n) =>
          scene.setNodes(n)
          scene.setEdges(e)
        }(ctx.owner)

      }
    )

    renderOnDomContentLoaded(
      dom.document.getElementById("app"),
      rootElement
    )
