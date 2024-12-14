package view

import com.raquo.airstream.ownership.Owner
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import domain.Node
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement
import state.GraphModel.*

import scala.scalajs.js.timers.setTimeout

final case class View(simulator: (Int, Int) => Unit):
  val scene: ThreeSceneImpl = ThreeSceneImpl(800, 800, 1000)
  private val renderTime: Var[Long] = Var(0)


  private def addRandomElementButton(): Element =
    button(
      "Add Random Node",
      h2(child.text <-- renderTime.signal.map(rt => s"Render time: $rt ms")),
      onClick --> (_ =>
        val now = System.currentTimeMillis()

          simulator(10000, 1)

          renderTime.set(System.currentTimeMillis() - now)
      )
    )

  def render(): Unit = {
    val rootElement = div(
      h1("ScaFi Web 3"),
      addRandomElementButton(),
      scene.renderScene(),
      onMountCallback { ctx =>
        implicit val owner: Owner = ctx.owner
        nodes.signal.combineWith(edges.signal).foreach {
          case (currentNodes, currentEdges) => scene.setNodes(currentNodes)
        }
      }
    )

    renderOnDomContentLoaded(
      dom.document.getElementById("app"),
      rootElement
    )
  }
