package model

object ModelModule:
  trait Model:
    def getEdges: Set[Edge]
    def getNodes: Set[Node]
    def addEdge(edge: Edge): Unit
    def addNode(node: Node): Unit
    def setNodes(newNodes: Set[Node]): Unit
  trait Provider:
    val model: Model
  trait Component:
    class ModelImpl extends Model:
      private var nodes: Set[Node] = Set.empty
      private var edges: Set[Edge] = Set.empty
      def getEdges: Set[Edge] = edges
      def getNodes: Set[Node] = nodes
      def addEdge(edge: Edge): Unit = edges += edge
      def addNode(node: Node): Unit = nodes += node; println("Added node")
      def setNodes(newNodes: Set[Node]): Unit = nodes = newNodes
  trait Interface extends Provider with Component