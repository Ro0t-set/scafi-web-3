package controller
import model.ModelModule
import model.SimpleNode
import model.Node
import view.ViewModule

import scala.scalajs.js
import scala.util.Random



object ControllerModule:
  trait Controller:
    def notifyChange(): Unit
    def generateRandomGraph(numberOfNode: Int, numberOfEdge: Int): Unit
    def getNodes: Set[Node]
    def getEdges: Set[(Node, Node)]
  trait Provider:
    val controller: Controller
  type Requirements = ViewModule.Provider with ModelModule.Provider
  trait Component:
    context: Requirements =>
    class ControllerImpl extends Controller:

      def getNodes: Set[Node] = context.model.getNodes

      def getEdges: Set[(Node, Node)] = context.model.getEdges

      def generateRandomGraph(numberOfNode: Int, numberOfEdge: Int): Unit =
        for _ <- 1 to numberOfNode do
          context.model.addNode(SimpleNode(Random.nextInt(100).toString, (Random.nextInt(800)- 400, Random.nextInt(800) - 400,  Random.nextInt(800)- 400), Random.nextInt(100).toString, 0xff0000))

        for _ <- 1 to numberOfEdge do
          val nodes = context.model.getNodes.toList
          val node1 = nodes(Random.nextInt(nodes.size))
          val node2 = nodes(Random.nextInt(nodes.size))
          context.model.addEdge((node1, node2))


      def notifyChange(): Unit = context.view.renderPage()
  trait Interface extends Provider with Component:
    self: Requirements =>