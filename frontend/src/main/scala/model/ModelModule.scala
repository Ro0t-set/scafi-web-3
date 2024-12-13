package model
import com.raquo.laminar.api.L.{*, given}
import model.Node
import model.Edge

object ModelModule:
  trait Model:
    def setEdge(edge: Set[Edge]): Unit
    def setNode(node: Set[Node]): Unit
    def getEdges: Signal[Set[Edge]]
    def getNodes: Signal[Set[Node]]
  trait Provider:
    val model: Model
  trait Component:
    class ModelImpl extends Model:
      val nodes: Var[Set[Node]] = Var(Set.empty)
      val edges: Var[Set[Edge]] = Var(Set.empty)
      def getEdges: Signal[Set[Edge]] = edges.signal
      def getNodes: Signal[Set[Node]] = nodes.signal
      def setEdge(edges: Set[Edge]): Unit = this.edges.update(_ => edges)
      def setNode(nodes: Set[Node]): Unit = this.nodes.update(_ => nodes)
  trait Interface extends Provider with Component