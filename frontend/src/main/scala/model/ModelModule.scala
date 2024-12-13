package model

type Id = String
final case class P3D(x: Double, y: Double, z: Double)
final case class Node(id: Id, p3D: P3D, label: String, color: Int)
final case class Edge(nodes: (Node, Node))

sealed trait Command

case class SetNodes(nodes: Set[Node]) extends Command
case class SetEdges(edges: Set[Edge]) extends Command
case class AddNode(node: Node) extends Command
case class AddEdge(edge: Edge) extends Command
case class RemoveNode(node: Node) extends Command
case class RemoveEdge(edge: Edge) extends Command

