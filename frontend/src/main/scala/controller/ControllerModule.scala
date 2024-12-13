package controller

import view.ViewModule
import scala.util.Random
import com.raquo.laminar.api.L.{*, given}
import model.{Edge, ModelModule, Node, SimpleNode}
import scala.scalajs.js.timers._


object ControllerModule:
  trait Controller:
    def notifyChange(): Unit
    def generateRandomGraph(numberOfNode: Int, numberOfEdge: Int): Unit
    def getNodes: Signal[Set[Node]]
    def getEdges: Signal[Set[Edge]]
  trait Provider:
    val controller: Controller
  type Requirements = ViewModule.Provider with ModelModule.Provider
  trait Component:
    context: Requirements =>
    class ControllerImpl extends Controller:

      def getNodes: Signal[Set[Node]] = context.model.getNodes

      def getEdges: Signal[Set[Edge]] = context.model.getEdges

      import scala.util.Random

      def generateRandomGraph(numberOfNodes: Int, numberOfEdges: Int): Unit = {
        // Inizializza un insieme vuoto di nodi
        var nodes: Set[Node] = Set.empty


        def addNode(id: Int): Unit = {
          val newNode = SimpleNode(
            id.toString,
            (
              Random.nextDouble() * 500, // Coordinata x casuale
              Random.nextDouble() * 500, // Coordinata y casuale
              Random.nextDouble() * 500  // Coordinata z casuale
            ),
            Random.nextInt(500).toString,
            Random.nextInt(500)
          )
          nodes += newNode
          context.model.setNode(nodes)
        }

        (1 to numberOfNodes).foreach { id =>
          setTimeout(2) {
            addNode(id)
          }
        }


      }



      def notifyChange(): Unit = context.view.renderPage()
  trait Interface extends Provider with Component:
    self: Requirements =>