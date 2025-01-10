package state

import com.raquo.laminar.api.L.*
import domain.{AnimationCommand, Edge, GraphCommand, Node, *}
import scala.scalajs.js

object GraphState:
  val nodes: Var[Set[Node]] = Var(Set.empty[Node])
  val edges: Var[Set[Edge]] = Var(Set.empty[Edge])

  private def foundNodeStream(id: Id): EventStream[Node] =
    nodes.signal
      .changes
      .map(_.find(_.id == id))
      .collect { case Some(node) => node }
      .take(1)

  def addEdgeWhenNodesFound(id1: Id, id2: Id): Unit =
    val combinedStream: EventStream[(Node, Node)] =
      foundNodeStream(id1).combineWith(foundNodeStream(id2))

    combinedStream.foreach { case (n1, n2) =>
      edges.update(_ + Edge((n1, n2)))
    }(unsafeWindowOwner)

  val commandObserver: Observer[GraphCommand] = Observer[GraphCommand] {
    case SetNodes(newNodes) =>
      nodes.set(newNodes)
      edges.update { currentEdges =>
        currentEdges.filter { edge =>
          val (n1, n2) = edge.nodes
          newNodes.contains(n1) && newNodes.contains(n2)
        }
      }

    case SetEdges(newEdges) =>
      val filtered = newEdges.filter { edge =>
        val (n1, n2) = edge.nodes
        nodes.now().contains(n1) && nodes.now().contains(n2)
      }
      edges.set(filtered)

    case SetEdgesByIds(edgesIds) =>
      edges.update { currentEdges =>
        val newEdges = edgesIds.flatMap { case (id1, id2) =>
          for
            n1 <- nodes.now().find(_.id == id1)
            n2 <- nodes.now().find(_.id == id2)
          yield Edge((n1, n2))
        }
        currentEdges ++ newEdges
      }

  }

object AnimationState:
  val running: Var[Boolean]           = Var[Boolean](false)
  val batch: Var[Int]                 = Var[Int](1)
  val currentTick: Var[Int]           = Var[Int](0)
  val engine: Var[Option[js.Dynamic]] = Var[Option[js.Dynamic]](None)
  val mode: Var[ViewMode]             = Var[ViewMode](ViewMode.Mode3D)
  private def reset(): Unit =
    running.set(false)
    currentTick.set(0)

  val animationObserver: Observer[AnimationCommand] =
    Observer[AnimationCommand] {
      case SetEngine(engine) =>
        this.engine.set(Some(engine))
        reset()
      case StartAnimation()      => if !running.now() then running.set(true)
      case PauseAnimation()      => running.set(false)
      case NextTick()            => currentTick.update(_ + 1)
      case AnimationBatch(batch) => this.batch.set(batch)
      case Reset()               => reset()
      case SwitchMode() => mode.update {
          case ViewMode.Mode2D => ViewMode.Mode3D
          case ViewMode.Mode3D => ViewMode.Mode2D
        }
    }
