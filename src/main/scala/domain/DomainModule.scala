package domain

import scala.scalajs.js

type Id    = Int
type Color = Int
type Label = String

final case class Position(x: Double, y: Double, z: Double)
final case class Node(id: Id, position: Position, label: Label, color: Color)
final case class Edge(nodes: (Node, Node)):
  override def equals(obj: Any): Boolean = obj match
    case that: Edge => this.nodes == that.nodes || this.nodes == that.nodes.swap
    case _          => false

sealed trait GraphCommand

case class SetNodes(nodes: Set[Node])             extends GraphCommand
case class SetEdges(edges: Set[Edge])             extends GraphCommand
case class SetEdgesByIds(edgesIds: Set[(Id, Id)]) extends GraphCommand
case class AddNode(node: Node)                    extends GraphCommand
case class AddEdge(edge: Edge)                    extends GraphCommand
case class AddEdgeByNodeId(node1: Id, node2: Id)  extends GraphCommand
case class RemoveNode(node: Node)                 extends GraphCommand
case class RemoveEdge(edge: Edge)                 extends GraphCommand

sealed trait AnimationCommand
case class SetEngine(engine: js.Dynamic) extends AnimationCommand
case class StartAnimation()              extends AnimationCommand
case class PauseAnimation()              extends AnimationCommand
case class NextTick()                    extends AnimationCommand
case class AnimationBatch(batch: Int)    extends AnimationCommand
case class Reset()                       extends AnimationCommand


sealed trait GridViewCommand
case class Set2dMode()   extends GridViewCommand
case class Set3dMode()   extends GridViewCommand
