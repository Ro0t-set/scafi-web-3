package state

import com.raquo.laminar.api.L.*
import domain.{Edge, GraphCommand, Node, SetEdges, SetEdgesByIds, SetNodes}

object GraphState:
  private val nodesVar: Var[Set[Node]] = Var(Set.empty[Node])
  private val edgesVar: Var[Set[Edge]] = Var(Set.empty[Edge])
  val nodes: Signal[Set[Node]]         = nodesVar.signal
  val edges: Signal[Set[Edge]]         = edgesVar.signal

  val commandObserver: Observer[GraphCommand] = Observer[GraphCommand] {

    case SetNodes(newNodes) =>
      nodesVar.set(newNodes)

    case SetEdges(newEdges) =>
      val filtered = newEdges.filter { edge =>
        val (n1, n2) = edge.nodes
        nodesVar.now().contains(n1) && nodesVar.now().contains(n2)
      }
      edgesVar.set(filtered)

    case SetEdgesByIds(edgesIds) =>
      edgesVar.set(Set.empty[Edge])
      edgesVar.update { currentEdges =>
        val newEdges = edgesIds.flatMap { case (id1, id2) =>
          for
            n1 <- nodesVar.now().find(_.id == id1)
            n2 <- nodesVar.now().find(_.id == id2)
          yield Edge((n1, n2))
        }
        currentEdges ++ newEdges
      }
  }
