package state

import com.raquo.laminar.api.L.*
import domain.{Node, Edge, GraphCommand, *}

object GraphState:
  val nodes: Var[Set[Node]] = Var(Set.empty[Node])
  val edges: Var[Set[Edge]] = Var(Set.empty[Edge])

  private def foundNode(id: Id): Option[Node] = nodes.now().find(_.id == id)

  val commandObserver: Observer[GraphCommand] = Observer[GraphCommand] {
    case SetNodes(newNodes)  => nodes.set(newNodes)
    case SetEdges(newEdges)  => edges.set(newEdges)
    case SetEdgesByIds(edgesIds) =>
      val foundEdges = edgesIds.flatMap {
        case (id1, id2) =>
          val foundNode1 = foundNode(id1)
          val foundNode2 = foundNode(id2)
          (foundNode1, foundNode2) match
            case (Some(n1), Some(n2)) => Some(Edge((n1, n2)))
            case _                    => None
      }
      edges.set(foundEdges)
    case AddNode(node)       => nodes.update(_ + node)
    case AddEdge(edge)       => edges.update(_ + edge)
    case AddEdgeByNodeId(node1, node2) =>
      val foundNode1 = foundNode(node1)
      val foundNode2 = foundNode(node2)
      (foundNode1, foundNode2) match
        case (Some(n1), Some(n2)) => edges.update(_ + Edge((n1, n2)))
        case _                    => ()
    case RemoveNode(node)    => nodes.update(_ - node)
    case RemoveEdge(edge)    => edges.update(_ - edge)
  }

