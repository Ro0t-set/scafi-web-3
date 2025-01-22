package state
import domain.GraphDomain.GraphEdge
import domain.GraphDomain.GraphNode
import domain.GraphDomain.Position
import domain.GraphDomain.SetEdges
import domain.GraphDomain.SetEdgesByIds
import domain.GraphDomain.SetNodes
import munit.FunSuite

class GraphStateSpec extends FunSuite:
  val node1: GraphNode  = GraphNode(1, Position(1, 1, 1), "Node1", 111111)
  val node2: GraphNode  = GraphNode(2, Position(2, 2, 2), "Node2", 222222)
  val node3: GraphNode  = GraphNode(3, Position(3, 3, 3), "Node3", 333333)
  val edge12: GraphEdge = GraphEdge((node1, node2))
  val edge23: GraphEdge = GraphEdge((node2, node3))
  val edge13: GraphEdge = GraphEdge((node1, node3))

  test("initial state should be empty") {
    assertEquals(
      GraphState.nodes.now(),
      Set.empty[GraphNode]
    )
    assertEquals(
      GraphState.edges.now(),
      Set.empty[GraphEdge]
    )
  }

  test("SetEdgesByIds should create edges between existing nodes") {
    // Setup nodes
    GraphState.commandObserver.onNext(SetNodes(Set(node1, node2, node3)))

    val edgeIds = Set(
      (1, 2),
      (2, 3),
      (1, 4)
    )

    GraphState.commandObserver.onNext(SetEdgesByIds(edgeIds))

    assertEquals(
      GraphState.edges.now(),
      Set(edge12, edge23),
      "Only edges between existing nodes should be created"
    )
  }

  test("SetEdgesByIds should handle duplicate edges") {
    GraphState.commandObserver.onNext(SetNodes(Set(node1, node2)))

    // Add same edge twice
    val edgeIds = Set((1, 2))
    GraphState.commandObserver.onNext(SetEdgesByIds(edgeIds))
    GraphState.commandObserver.onNext(SetEdgesByIds(edgeIds))

    assertEquals(
      GraphState.edges.now(),
      Set(edge12),
      "Duplicate edges should not be added"
    )
  }

  override def beforeEach(context: BeforeEach): Unit =
    GraphState.commandObserver.onNext(SetNodes(Set.empty))
    GraphState.commandObserver.onNext(SetEdges(Set.empty))
