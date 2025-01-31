package view.graph.extensions

import domain.GraphDomain.GraphEdge
import domain.GraphDomain.GraphNode

object DomainExtensions:
  extension (edge: GraphEdge)
    def object3dName: String =
      val (n1, n2) = edge.nodes
      val (minId, maxId) =
        if n1.id < n2.id then (n1.id, n2.id) else (n2.id, n1.id)
      s"edge-$minId-$maxId-$n1-$n2"

  extension (node: GraphNode)
    def object3dName: String = s"node-${node.id}"
