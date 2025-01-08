package view

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import domain.{setEngine, AnimationBatch, PauseAnimation, StartAnimation}
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement
import state.AnimationState.{animationObserver, batch, currentTick}
import state.GraphState.{edges, nodes}
import view.graph.ThreeSceneImpl

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal("scastie.ClientMain")
object ClientMain extends js.Object:
  val signal: js.Function3[js.Any, js.Any, js.Any, Unit] = js.native

final case class View():
  private val windowsWidth: Int  = 700
  private val windowsHeight: Int = 400

  val scene: ThreeSceneImpl =
    ThreeSceneImpl(windowsWidth, windowsHeight, 1000)

  private val xVar        = Var(10)
  private val yVar        = Var(10)
  private val zVar        = Var(2)
  private val distXVar    = Var(100)
  private val distYVar    = Var(100)
  private val distZVar    = Var(100)
  private val edgeDistVar = Var(190)

  private def loadEngine(): Unit =
    animationObserver.onNext(PauseAnimation())

    val newEngine = js.Dynamic.global.EngineImpl(
      xVar.now(),
      yVar.now(),
      zVar.now(),
      distXVar.now(),
      distYVar.now(),
      distZVar.now(),
      edgeDistVar.now()
    )

    animationObserver.onNext(setEngine(newEngine))

  private val engineSettingsView: Element =
    div(
      cls := "engine-form-layout",
      h3("Engine Settings"),
      div(
        label("x: "),
        input(
          typ   := "number",
          value := xVar.now().toString,
          onInput.mapToValue.map(_.toInt) --> xVar
        ),
        label("y: "),
        input(
          typ   := "number",
          value := yVar.now().toString,
          onInput.mapToValue.map(_.toInt) --> yVar
        ),
        label("z: "),
        input(
          typ   := "number",
          value := zVar.now().toString,
          onInput.mapToValue.map(_.toInt) --> zVar
        ),
        label("distX: "),
        input(
          typ   := "number",
          value := distXVar.now().toString,
          onInput.mapToValue.map(_.toInt) --> distXVar
        ),
        label("distY: "),
        input(
          typ   := "number",
          value := distYVar.now().toString,
          onInput.mapToValue.map(_.toInt) --> distYVar
        ),
        label("distZ: "),
        input(
          typ   := "number",
          value := distZVar.now().toString,
          onInput.mapToValue.map(_.toInt) --> distZVar
        ),
        label("edgeDist: "),
        input(
          typ   := "number",
          value := edgeDistVar.now().toString,
          onInput.mapToValue.map(_.toInt) --> edgeDistVar
        )
      ),
      button(
        "Load Parameters",
        onClick --> { _ => loadEngine() }
      )
    )

  private def initialize(): Unit =
    val originalSignal = ClientMain.signal

    def newSignal(
        result: js.Any,
        attachedElements: js.Any,
        scastieId: js.Any
    ): Unit =
      loadEngine()
      originalSignal(result, attachedElements, scastieId)

    js.Dynamic.global.scastie.ClientMain.signal = newSignal

  private val animationControllerView: Element =
    div(
      cls := "animation-controller",
      div(
        cls := "info-section",
        p(
          strong("Batch: "),
          child.text <-- batch.signal.map(_.toString),
          " | ",
          strong("Counter: "),
          child.text <-- currentTick.signal.map(_.toString)
        )
      ),
      br(),
      div(
        cls := "controls",
        button(
          cls := "control-button",
          i(cls := "fas fa-play"),
          " Start",
          onClick --> (_ => animationObserver.onNext(StartAnimation()))
        ),
        button(
          cls := "control-button",
          i(cls := "fas fa-pause"),
          " Pause",
          onClick --> (_ => animationObserver.onNext(PauseAnimation()))
        )
      ),
      div(
        cls := "slider-container",
        label(
          i(cls := "fas fa-sliders-h"),
          " Animation Batch"
        ),
        input(
          idAttr  := "batch-slider",
          `type`  := "range",
          minAttr := "1",
          maxAttr := "512",
          value   := "1",
          onInput.mapToValue.map(_.toInt) --> (batchSize =>
            animationObserver.onNext(AnimationBatch(batchSize))
          )
        )
      )
    )

  def render(): Unit =
    val rootElement = div(
      scene.renderScene("three_canvas"),
      engineSettingsView,
      animationControllerView,
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
