package view

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import domain.Node
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement
import state.GraphState.*
import scala.scalajs.js.timers.{setInterval, setTimeout}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

final case class View(simulator: (Int, Int) => Unit):
  val scene: ThreeSceneImpl = ThreeSceneImpl(800, 800, 1000)
  private val renderTime: Var[Long] = Var(0)


  private def addRandomElementButton(): Element =
    button(
      "Add Random Node",
      h2(child.text <-- renderTime.signal.map(rt => s"Render time: $rt ms")),
      onClick --> (_ =>
        val now = System.currentTimeMillis()
        Future{
          simulator(10000, 0)
        }.onComplete {
          case Success(_) => renderTime.set(System.currentTimeMillis() - now)
          case Failure(exception) => println(s"Execution failed with exception: $exception")
        }
      )
    )

  def render(): Unit = {
    val rootElement = div(
      h1("ScaFi Web 3"),
      addRandomElementButton(),
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
