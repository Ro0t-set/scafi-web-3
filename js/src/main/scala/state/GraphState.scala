package state

import com.raquo.laminar.api.L._
import domain.GraphDomain._

trait GraphState:
  val nodes: StrictSignal[Set[GraphNode]]
  val edges: StrictSignal[Set[GraphEdge]]
  val commandObserver: Observer[GraphCommand]

object GraphState extends GraphState:
  private val nodesVar: Var[Set[GraphNode]]        = Var(Set.empty[GraphNode])
  private val edgesVar: Var[Set[GraphEdge]]        = Var(Set.empty[GraphEdge])
  override val nodes: StrictSignal[Set[GraphNode]] = nodesVar.signal
  override val edges: StrictSignal[Set[GraphEdge]] = edgesVar.signal

  override val commandObserver: Observer[GraphCommand] =
    Observer[GraphCommand] {

      case SetNodes(newNodes) =>
        nodesVar.set(newNodes)

      case SetEdges(newEdges) =>
        val filtered = newEdges.filter { edge =>
          val (n1, n2) = edge.nodes
          nodesVar.now().contains(n1) && nodesVar.now().contains(n2)
        }
        edgesVar.set(filtered)

      case SetEdgesByIds(edgesIds) =>
        edgesVar.set(Set.empty[GraphEdge])
        edgesVar.update { currentEdges =>
          val newEdges = edgesIds.flatMap { case (id1, id2) =>
            for
              n1 <- nodesVar.now().find(_.id == id1)
              n2 <- nodesVar.now().find(_.id == id2)
            yield GraphEdge((n1, n2))
          }
          currentEdges ++ newEdges
        }
    }
