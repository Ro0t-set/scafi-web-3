package state

import domain.GraphDomain.GraphEdge
import domain.GraphDomain.GraphNode
import domain.GraphDomain.Position
import domain.GraphDomain.SetEdges
import domain.GraphDomain.SetEdgesByIds
import domain.GraphDomain.SetNodes
import munit.FunSuite
import munit.ScalaCheckSuite
import org.scalacheck.Prop.forAll

class GraphStateSpec extends FunSuite with ScalaCheckSuite:

  override def beforeEach(context: BeforeEach): Unit =
    GraphState.commandObserver.onNext(SetNodes(Set.empty))
    GraphState.commandObserver.onNext(SetEdges(Set.empty))

  test("addNode should add a node to the state") {
    forAll {
      (id: Int, label: String, color: Int, x: Double, y: Double, z: Double) =>
        val node: Set[GraphNode] =
          Set(GraphNode(id, Position(x, y, z), label, color))
        GraphState.commandObserver.onNext(SetNodes(node))
        val result: Set[GraphNode] = GraphState.nodes.now()
        result == node
    }
  }

  test("addEdge should add an edge to the state") {
    forAll {
      (
          id1: Int,
          label1: String,
          color1: Int,
          x1: Double,
          y1: Double,
          z1: Double,
          id2: Int,
          label2: String,
          color2: Int,
          x2: Double,
          y2: Double,
          z2: Double
      ) =>
        val node1 = GraphNode(id1, Position(x1, y1, z1), label1, color1)
        val node2 = GraphNode(id2, Position(x2, y2, z2), label2, color2)
        val nodes = Set(node1, node2)
        val edge  = Set(GraphEdge((node1, node2)))

        GraphState.commandObserver.onNext(SetNodes(nodes))
        GraphState.commandObserver.onNext(SetEdges(edge))

        val result: Set[GraphEdge] = GraphState.edges.now()
        result == edge
    }
  }

  test("addEdgeById should add an edge to the state") {
    forAll {
      (
          id1: Int,
          label1: String,
          color1: Int,
          x1: Double,
          y1: Double,
          z1: Double
      ) =>
        val id2   = id1 + 1
        val node1 = GraphNode(id1, Position(x1, y1, z1), label1, color1)
        val node2 = GraphNode(id2, Position(x1, y1, z1), label1, color1)
        val nodes = Set(node1, node2)

        GraphState.commandObserver.onNext(SetNodes(nodes))

        GraphState.commandObserver.onNext(SetEdgesByIds(Set((id1, id2))))

        val result: Set[GraphEdge] = GraphState.edges.now()

        result == Set(GraphEdge((node1, node2)))
    }
  }

  test("edges can be added only if the nodes exist") {
    forAll {
      (
          id1: Int,
          label1: String,
          color1: Int,
          x1: Double,
          y1: Double,
          z1: Double
      ) =>

        val node1         = GraphNode(id1, Position(x1, y1, z1), label1, color1)
        val nonExistentId = id1 + 1

        GraphState.commandObserver.onNext(SetNodes(Set(node1)))
        GraphState.commandObserver.onNext(SetEdgesByIds(Set((
          id1,
          nonExistentId
        ))))

        val result: Set[GraphEdge] = GraphState.edges.now()
        result == Set.empty[GraphEdge]
    }
  }
