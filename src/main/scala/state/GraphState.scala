package state

import com.raquo.laminar.api.L.*
import domain.{Edge, GraphCommand, Id, Node, SetEdges, SetEdgesByIds, SetNodes}

object GraphState:
  private val nodesVar: Var[Set[Node]] = Var(Set.empty[Node])
  private val edgesVar: Var[Set[Edge]] = Var(Set.empty[Edge])
  val nodes: Signal[Set[Node]]         = nodesVar.signal
  val edges: Signal[Set[Edge]]         = edgesVar.signal

  private def foundNodeStream(id: Id): EventStream[Node] =
    nodesVar.signal
      .changes
      .map(_.find(_.id == id))
      .collect { case Some(node) => node }
      .take(1)

  val commandObserver: Observer[GraphCommand] = Observer[GraphCommand] {

    case SetNodes(newNodes) =>
      nodesVar.set(newNodes)
      edgesVar.update { currentEdges =>
        currentEdges.filter { edge =>
          val (n1, n2) = edge.nodes
          newNodes.contains(n1) && newNodes.contains(n2)
        }
      }

    case SetEdges(newEdges) =>
      val filtered = newEdges.filter { edge =>
        val (n1, n2) = edge.nodes
        nodesVar.now().contains(n1) && nodesVar.now().contains(n2)
      }
      edgesVar.set(filtered)

    case SetEdgesByIds(edgesIds) =>
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
