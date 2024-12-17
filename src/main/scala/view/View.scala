package view

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import domain.{AnimationBatch, PauseAnimation, StartAnimation}
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement
import state.GraphState.{edges, nodes}
import state.AnimationState.{animationObserver, batch, currentTick}


final case class View():
  val scene: ThreeSceneImpl = ThreeSceneImpl(800, 800, 1000)

  private def animationControllerView(): Element =
    div(
      //Render batch and counter from animationObserver.batch

      p("Batch: ", child.text <-- batch.signal.map(_.toString)),
      p("Counter: ", child.text <-- currentTick.signal.map(_.toString)),
      br(),
      button("Start", onClick --> (_ => animationObserver.onNext(StartAnimation()))),
      button("Pause", onClick --> (_ => animationObserver.onNext(PauseAnimation()))),
      //slider
      input(
        `type` := "range",
        `minAttr`:="1",
        `maxAttr`:="100",
        value := "1",
        onInput.mapToValue.map(_.toInt) --> (batch => animationObserver.onNext(AnimationBatch(batch)))
      ),
      //button("Next", onClick --> (_ => animationObserver.onNext(nextTick()))),
      //button("Reset", onClick --> (_ => animationObserver.onNext(Reset())))
    )

  def render(): Unit = {
    val rootElement = div(
      h1("ScaFi Web 3"),
      animationControllerView(),
      scene.renderScene(),
      onMountCallback { ctx =>
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
  }
