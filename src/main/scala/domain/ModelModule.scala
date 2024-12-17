package domain

type Id = Int
type Color = Int
type Label = String

final case class Position(x: Double, y: Double, z: Double)
final case class Node(id: Id, position: Position, label: Label, color: Color):
  override def equals(obj: Any): Boolean = obj match
    case that: Node => this.id == that.id
    case _ => false
final case class Edge(nodes: (Node, Node))

sealed trait Command

case class SetNodes(nodes: Set[Node]) extends Command
case class SetEdges(edges: Set[Edge]) extends Command
case class SetEdgesByIds(edgesIds: Set[(Id, Id)]) extends Command
case class AddNode(node: Node) extends Command
case class AddEdge(edge: Edge) extends Command
case class AddEdgeByNodeId(node1: Id, node2: Id) extends Command
case class RemoveNode(node: Node) extends Command
case class RemoveEdge(edge: Edge) extends Command

