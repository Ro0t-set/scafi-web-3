package view

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import state.GraphState.{edges, nodes}
import view.components.{
  AnimationControllerView,
  EngineSettingsView,
  GridViewControllerView
}
import view.config.ViewConfig
import view.controller.EngineController
import view.graph.ThreeSceneImpl

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
      onMountCallback { _ =>
        initialize()
        nodes.signal.combineWith(edges.signal).foreach {
          case (currentNodes, currentEdges) =>
            scene.setNodes(currentNodes)
            scene.setEdges(currentEdges)
        }(unsafeWindowOwner)

      }
    )

    renderOnDomContentLoaded(
      dom.document.getElementById("app"),
      rootElement
    )
