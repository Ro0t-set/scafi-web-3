package domain

import scala.scalajs.js

type Id    = Int
type Color = Int
type Label = String

enum ViewMode:
  case Mode2D, Mode3D

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

sealed trait AnimationCommand
case class SetEngine(engine: js.Dynamic) extends AnimationCommand
case class StartAnimation()              extends AnimationCommand
case class PauseAnimation()              extends AnimationCommand
case class NextTick()                    extends AnimationCommand
case class NextTickAdd(tick: Int)        extends AnimationCommand
case class AnimationBatch(batch: Int)    extends AnimationCommand
case class Reset()                       extends AnimationCommand
case class SwitchMode()                  extends AnimationCommand
