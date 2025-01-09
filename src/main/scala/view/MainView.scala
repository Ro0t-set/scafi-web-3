package view

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import state.GraphState.{edges, nodes}
import view.components.{AnimationControllerView, EngineSettingsView}
import view.config.ViewConfig
import view.controller.EngineController
import view.graph.ThreeSceneImpl

final class MainView(config: ViewConfig):
  private val scene = ThreeSceneImpl(config.sceneConfig)
  private val engineController = new EngineController(
    Var(10),
    Var(10),
    Var(2),
    Var(100),
    Var(100),
    Var(100),
    Var(190)
  )
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
      originalSignal(result, attachedElements, scastieId)
      println("Engine loaded")

    scala.scalajs.js.Dynamic.global.scastie.ClientMain.signal = newSignal

  def render(): Unit =
    val rootElement = div(
      scene.renderScene("three_canvas"),
      engineSettings.render,
      animationController.render,
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
