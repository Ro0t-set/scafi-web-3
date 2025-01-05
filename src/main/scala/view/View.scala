package view

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import domain.{setEngine, AnimationBatch, PauseAnimation, StartAnimation}
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement
import state.GraphState.{edges, nodes}
import state.AnimationState.{animationObserver, batch, currentTick}
import view.graph.ThreeSceneImpl

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal("scastie.ClientMain")
object ClientMain extends js.Object:
  val signal: js.Function3[js.Any, js.Any, js.Any, Unit] = js.native

@SuppressWarnings(Array("org.wartremover.warts.All"))
final case class View():
  private val windowsWidth: Int  = 600
  private val windowsHeight: Int = 600

  val scene: ThreeSceneImpl =
    ThreeSceneImpl(windowsWidth, windowsHeight, 1000)

  private def initialize(): Unit =
    val originalSignal = ClientMain.signal

    def newSignal(
        result: js.Any,
        attachedElements: js.Any,
        scastieId: js.Any
    ): Unit =
      animationObserver.onNext(PauseAnimation())

      val newEngine =
        js.Dynamic.global.EngineImpl(5, 5, 2, 100, 100, 100, 190)

      animationObserver.onNext(setEngine(newEngine))

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
          maxAttr := "100",
          value   := "1",
          onInput.mapToValue.map(_.toInt) --> (batch =>
            animationObserver.onNext(AnimationBatch(batch))
          )
        )
      )
    )

  def render(): Unit =
    val rootElement = div(
      scene.renderScene("three_canvas"),
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
