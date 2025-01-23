package view

import com.raquo.laminar.api.L._
import org.scalajs.dom
import state.AnimationState.engine
import state.AnimationState.running
import state.GraphState.edges
import state.GraphState.nodes
import view.components.AnimationControllerView
import view.components.EngineSettingsControllerView
import view.components.GridViewControllerView
import view.config.ViewConfig
import view.graph.scene.ThreeScene
import view.player.EngineController
import view.player.EngineLoopPlayer

final class MainView(config: ViewConfig):
  private val scene               = ThreeScene(config.sceneConfig)
  private val sceneController     = GridViewControllerView(scene)
  private val engineController    = EngineController()()()
  private val engineSettings      = EngineSettingsControllerView(engineController)
  private val animationController = AnimationControllerView()
  private val player              = EngineLoopPlayer

  private def initialize(): Unit =
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
