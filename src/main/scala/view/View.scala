package view

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import domain.{AnimationBatch, PauseAnimation, StartAnimation}
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement
import state.GraphState.{edges, nodes}
import state.AnimationState.{animationObserver, batch, currentTick}

final case class View():
  val scene: ThreeSceneImpl = ThreeSceneImpl(600, 600, 1000)

  private def animationControllerView(): Element =
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
          i(cls := "fas fa-play"), // Font Awesome Play Icon
          " Start",
          onClick --> (_ => animationObserver.onNext(StartAnimation()))
        ),
        button(
          cls := "control-button",
          i(cls := "fas fa-pause"), // Font Awesome Pause Icon
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
      animationControllerView(),
      onMountCallback { _ =>
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
