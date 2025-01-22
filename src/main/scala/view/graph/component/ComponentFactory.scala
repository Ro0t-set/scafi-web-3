package view.graph.component
import domain.GraphDomain.GraphEdge
import domain.GraphDomain.GraphNode
import view.graph.adapter.ThreeGroup
import view.graph.extensions.DomainExtensions._

object ComponentFactory:
  object NodeFactory:
    def apply(
        node: GraphNode
    ): ThreeGroup =
      Node3dObj(
        node.id.toString,
        node.label,
        node.color,
        node.object3dName
      )(
        node.position.x,
        node.position.y,
        node.position.z
      )
  object EdgeFactory:
    def apply(
        edge: GraphEdge
    ): ThreeGroup =
      val (node1, node2) = edge.nodes
      Edge3dObj(node1.position.x, node2.position.x)(
        node1.position.y,
        node2.position.y
      )(node1.position.z, node2.position.z)(
        edge.object3dName
      )
