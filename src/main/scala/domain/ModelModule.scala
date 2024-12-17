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

sealed trait GraphCommand

case class SetNodes(nodes: Set[Node]) extends GraphCommand
case class SetEdges(edges: Set[Edge]) extends GraphCommand
case class SetEdgesByIds(edgesIds: Set[(Id, Id)]) extends GraphCommand
case class AddNode(node: Node) extends GraphCommand
case class AddEdge(edge: Edge) extends GraphCommand
case class AddEdgeByNodeId(node1: Id, node2: Id) extends GraphCommand
case class RemoveNode(node: Node) extends GraphCommand
case class RemoveEdge(edge: Edge) extends GraphCommand


sealed trait AnimationCommand

case class StartAnimation() extends AnimationCommand
case class PauseAnimation() extends AnimationCommand
case class nextTick() extends AnimationCommand
case class AnimationBatch(batch: Int) extends AnimationCommand
case class Reset() extends AnimationCommand