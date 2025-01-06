package state

import munit.FunSuite
import com.raquo.laminar.api.L._
import domain.{Node, *}

class GraphStateSpec extends FunSuite:

  private def makeNode(
      id: Int,
      x: Double,
      y: Double,
      z: Double,
      label: String,
      color: Int
  ): Node =
    Node(id, Position(x, y, z), label, color)

  override def beforeEach(context: BeforeEach): Unit = {
    GraphState.nodes.set(Set.empty)
    GraphState.edges.set(Set.empty)
  }

  test("SetNodes sets the nodes Var to the given nodes") {
    val nodeA = makeNode(1, 0.0, 0.0, 0.0, "A", 0x123456)
    val nodeB = makeNode(2, 1.0, 1.0, 1.0, "B", 0x654321)
    val cmd   = SetNodes(Set(nodeA, nodeB))

    GraphState.commandObserver.onNext(cmd)

    assertEquals(
      GraphState.nodes.now(),
      Set(nodeA, nodeB),
      "GraphState.nodes should contain the two nodes we just set"
    )
  }

  test("SetEdges sets the edges Var to the given edges") {
    val nodeA = makeNode(1, 0.0, 0.0, 0.0, "A", 0x123456)
    val nodeB = makeNode(2, 1.0, 1.0, 1.0, "B", 0x654321)
    val nodeC = makeNode(3, 2.0, 2.0, 2.0, "C", 0xff0000)
    val edge1 = Edge((nodeA, nodeB))
    val edge2 = Edge((nodeB, nodeC))
    val cmd   = SetEdges(Set(edge1, edge2))

    GraphState.commandObserver.onNext(cmd)

    assertEquals(
      GraphState.edges.now(),
      Set(edge1, edge2),
      "GraphState.edges should contain the two edges we just set"
    )
  }

  test("SetEdgesByIds replaces edges based on existing node IDs") {
    val nodeA = makeNode(1, 0.0, 0.0, 0.0, "A", 0x111111)
    val nodeB = makeNode(2, 1.0, 1.0, 1.0, "B", 0x222222)
    val nodeC = makeNode(3, 2.0, 2.0, 2.0, "C", 0x333333)
    GraphState.nodes.set(Set(nodeA, nodeB, nodeC))

    // Now set edges by IDs
    val cmd = SetEdgesByIds(Set((1, 2), (1, 3)))
    GraphState.commandObserver.onNext(cmd)

    val expectedEdges = Set(
      Edge((nodeA, nodeB)),
      Edge((nodeA, nodeC))
    )

    assertEquals(
      GraphState.edges.now(),
      expectedEdges,
      "GraphState.edges should be replaced with edges computed from existing nodes"
    )
  }

  test("AddNode adds a new node to the existing nodes") {
    val nodeA = makeNode(1, 0.0, 0.0, 0.0, "A", 0x111111)
    GraphState.nodes.set(Set(nodeA))

    val nodeB = makeNode(2, 1.0, 1.0, 1.0, "B", 0x222222)
    val cmd   = AddNode(nodeB)
    GraphState.commandObserver.onNext(cmd)

    assertEquals(
      GraphState.nodes.now(),
      Set(nodeA, nodeB),
      "GraphState.nodes should contain the old node plus the new node"
    )
  }

  test("AddEdge adds a new edge to the existing edges") {
    val nodeA = makeNode(1, 0.0, 0.0, 0.0, "A", 0x111111)
    val nodeB = makeNode(2, 1.0, 1.0, 1.0, "B", 0x222222)
    val nodeC = makeNode(3, 2.0, 2.0, 2.0, "C", 0x333333)
    val edge1 = Edge((nodeA, nodeB))
    GraphState.edges.set(Set(edge1))

    val edge2 = Edge((nodeB, nodeC))
    val cmd   = AddEdge(edge2)
    GraphState.commandObserver.onNext(cmd)

    assertEquals(
      GraphState.edges.now(),
      Set(edge1, edge2),
      "GraphState.edges should contain the old edge plus the new edge"
    )
  }

  test("AddEdgeByNodeId adds an edge if both nodes exist") {
    val nodeA = makeNode(1, 0.0, 0.0, 0.0, "A", 0x111111)
    val nodeB = makeNode(2, 1.0, 1.0, 1.0, "B", 0x222222)
    GraphState.nodes.set(Set(nodeA, nodeB))

    val cmd = AddEdgeByNodeId(1, 2)
    GraphState.commandObserver.onNext(cmd)

    assertEquals(
      GraphState.edges.now(),
      Set(Edge((nodeA, nodeB))),
      "GraphState.edges should contain the newly created edge"
    )
  }

  test("AddEdgeByNodeId does nothing if one of the nodes does not exist") {

    val nodeA = makeNode(1, 0.0, 0.0, 0.0, "A", 0x111111)
    GraphState.nodes.set(Set(nodeA))

    val cmd = AddEdgeByNodeId(1, 2)
    GraphState.commandObserver.onNext(cmd)

    assertEquals(
      GraphState.edges.now(),
      Set.empty[Edge],
      "No edge should be added if one of the nodes doesn't exist"
    )
  }

  test("RemoveNode removes the given node") {
    val nodeA = makeNode(1, 0.0, 0.0, 0.0, "A", 0x111111)
    val nodeB = makeNode(2, 1.0, 1.0, 1.0, "B", 0x222222)
    GraphState.nodes.set(Set(nodeA, nodeB))

    val cmd = RemoveNode(nodeB)
    GraphState.commandObserver.onNext(cmd)

    assertEquals(
      GraphState.nodes.now(),
      Set(nodeA),
      "GraphState.nodes should remove nodeB"
    )
  }

  test("RemoveEdge removes the given edge") {
    val nodeA = makeNode(1, 0.0, 0.0, 0.0, "A", 0x111111)
    val nodeB = makeNode(2, 1.0, 1.0, 1.0, "B", 0x222222)
    val edge1 = Edge((nodeA, nodeB))
    GraphState.edges.set(Set(edge1))

    val cmd = RemoveEdge(edge1)
    GraphState.commandObserver.onNext(cmd)

    assertEquals(
      GraphState.edges.now(),
      Set.empty[Edge],
      "GraphState.edges should remove the given edge"
    )

  }
