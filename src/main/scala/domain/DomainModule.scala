package domain

sealed trait GraphType:
  type Id    = Int
  type Color = Int
  type Label = String

object GraphDomain extends GraphType:
  final case class Position(x: Double, y: Double, z: Double)
  final case class GraphNode(
      id: Id,
      position: Position,
      label: Label,
      color: Color
  )
  final case class GraphEdge(nodes: (GraphNode, GraphNode)):
    override def equals(obj: Any): Boolean = obj match
      case that: GraphEdge =>
        this.nodes == that.nodes || this.nodes == that.nodes.swap
      case _ => false

  sealed trait GraphCommand
  case class SetNodes(nodes: Set[GraphNode])        extends GraphCommand
  case class SetEdges(edges: Set[GraphEdge])        extends GraphCommand
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
