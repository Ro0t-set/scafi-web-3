package state

import com.raquo.laminar.api.L.*
import domain.{Node, *}

object GraphModel:
  val nodes: Var[Set[Node]] = Var(Set.empty[Node])
  val edges: Var[Set[Edge]] = Var(Set.empty[Edge])

  val commandObserver: Observer[Command] = Observer[Command] {
    case SetNodes(newNodes)  => nodes.set(newNodes)
    case SetEdges(newEdges)  => edges.set(newEdges)
    case AddNode(node)       => nodes.update(_ + node)
    case AddEdge(edge)       => edges.update(_ + edge)
    case RemoveNode(node)    => nodes.update(_ - node)
    case RemoveEdge(edge)    => edges.update(_ - edge)
  }
