package view

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import model.{Edge, Node, Position}
import model.*
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement
import com.raquo.airstream.ownership.Owner

import scala.scalajs.js.timers.setTimeout

private val nodes = Var(Set.empty[Node])
private val edges = Var(Set.empty[Edge])

private val commandObserver = Observer[Command] {
  case SetNodes(newNodes)  => nodes.set(newNodes)
  case SetEdges(newEdges)  => edges.set(newEdges)
  case AddNode(node)       => nodes.update(_ + node)
  case AddEdge(edge)       => edges.update(_ + edge)
  case RemoveNode(node)    => nodes.update(_ - node)
  case RemoveEdge(edge)    => edges.update(_ - edge)
}

final case class Mvc():
  val scene: ThreeSceneImpl = ThreeSceneImpl(800, 800, 1000)
  private val renderTime: Var[Long] = Var(0)

  private def addRandomNode(): Unit = {
    val id = Math.random().toString
    val x = Math.random() * 800 - 400
    val y = Math.random() * 800 - 400
    val z = Math.random() * 800 - 400
    val node = Node(id, Position(x, y, z), "i", 0x00ff00)
    commandObserver.onNext(AddNode(node))
  }

  private def addRandomElementButton(): Element =
    button(
      "Add Random Node",
      h2(child.text <-- renderTime.signal.map(rt => s"Render time: $rt ms")),
      onClick --> (_ =>
        val now = System.currentTimeMillis()
        for (_ <- 1 to 200) yield
          setTimeout(5) {
            addRandomNode()
            renderTime.set(System.currentTimeMillis() - now)
          }
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
