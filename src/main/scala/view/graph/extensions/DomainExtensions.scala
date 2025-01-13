package view.graph.extensions

import domain.Edge
import domain.Node

object DomainExtensions:
  extension (edge: Edge)
    def object3dName: String =
      val (n1, n2) = edge.nodes
      val (minId, maxId) =
        if n1.id < n2.id then (n1.id, n2.id) else (n2.id, n1.id)
      s"edge-$minId-$maxId-$n1-$n2"

  extension (node: Node)
    def object3dName: String = s"node-${node.id}"
