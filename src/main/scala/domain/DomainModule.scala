package domain



sealed trait GraphType:
  type Id    = Int
  type Color = Int
  type Label = String

object GraphDomain extends GraphType:
  final case class Position(x: Double, y: Double, z: Double)
  final case class Node(id: Id, position: Position, label: Label, color: Color)
  final case class Edge(nodes: (Node, Node)):
    override def equals(obj: Any): Boolean = obj match
      case that: Edge =>
        this.nodes == that.nodes || this.nodes == that.nodes.swap
      case _ => false

  sealed trait GraphCommand
  case class SetNodes(nodes: Set[Node])             extends GraphCommand
  case class SetEdges(edges: Set[Edge])             extends GraphCommand
  case class SetEdgesByIds(edgesIds: Set[(Id, Id)]) extends GraphCommand

object AnimationDomain:
  enum ViewMode:
    case Mode2D, Mode3D
  sealed trait AnimationCommand[Engine]
  case class SetEngine[Engine](engine: Engine)  extends AnimationCommand[Engine]
  case class StartAnimation[Engine]()           extends AnimationCommand[Engine]
  case class PauseAnimation[Engine]()           extends AnimationCommand[Engine]
  case class NextTick[Engine]()                 extends AnimationCommand[Engine]
  case class NextTickAdd[Engine](tick: Int)     extends AnimationCommand[Engine]
  case class AnimationBatch[Engine](batch: Int) extends AnimationCommand[Engine]
  case class Reset[Engine]()                    extends AnimationCommand[Engine]
  case class SwitchMode[Engine]()               extends AnimationCommand[Engine]
