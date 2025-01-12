package view

import com.raquo.airstream.ownership.TransferableSubscription
import com.raquo.laminar.api.L.*
import domain.{Edge, Node}
import org.scalajs.dom
import org.scalajs.dom.console
import state.GraphState.{edges, nodes}
import view.components.{AnimationControllerView, EngineSettingsView, GridViewControllerView}
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
        

        val dynOwner = new DynamicOwner(() => console.warn("Owner destroyed"))
        val observerGraph: Observer[(Set[Edge], Set[Node])] =
          Observer[(Set[Edge], Set[Node])](graph => {
            scene.setNodes(graph._2)
            scene.setEdges(graph._1)
          })
        val dynSub = DynamicSubscription.unsafe(
          dynOwner,
          activate = (owner: Owner) =>
            edges.combineWith(nodes).addObserver(observerGraph)(owner)
        )
        dynOwner.activate()
        EngineControllerPlayer.Player(ctx.owner, dynOwner)
      }
    )

    renderOnDomContentLoaded(
      dom.document.getElementById("app"),
      rootElement
    )
