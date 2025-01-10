//package state
//
//import munit.FunSuite
//import com.raquo.laminar.api.L._
//import domain.{Node, *}
//
//class GraphStateSpec extends FunSuite:
//
//  private def makeNode(
//      id: Int,
//      x: Double,
//      y: Double,
//      z: Double,
//      label: String,
//      color: Int
//  ): Node =
//    Node(id, Position(x, y, z), label, color)
//
//  test("SetNodes sets the nodes Var to the given nodes") {
//    val nodeA = makeNode(1, 0.0, 0.0, 0.0, "A", 0x123456)
//    val nodeB = makeNode(2, 1.0, 1.0, 1.0, "B", 0x654321)
//    val cmd   = SetNodes(Set(nodeA, nodeB))
//
//    GraphState.commandObserver.onNext(cmd)
//
//    assertEquals(
//      GraphState.nodes.now(),
//      Set(nodeA, nodeB),
//      "GraphState.nodes should contain the two nodes we just set"
//    )
//  }
//
//  test("SetEdges sets the edges Var to the given edges") {
//    val nodeA = makeNode(1, 0.0, 0.0, 0.0, "A", 0x123456)
//    val nodeB = makeNode(2, 1.0, 1.0, 1.0, "B", 0x654321)
//    val nodeC = makeNode(3, 2.0, 2.0, 2.0, "C", 0xff0000)
//    GraphState.commandObserver.onNext(SetNodes(Set(nodeA, nodeB, nodeC)))
//    val edge1 = Edge((nodeA, nodeB))
//    val edge2 = Edge((nodeB, nodeC))
//    val cmd   = SetEdges(Set(edge1, edge2))
//
//    GraphState.commandObserver.onNext(cmd)
//
//    assertEquals(
//      GraphState.edges.now(),
//      Set(edge1, edge2),
//      "GraphState.edges should contain the two edges we just set"
//    )
//  }
//
//  test("SetEdgesByIds replaces edges based on existing node IDs") {
//    val nodeA = makeNode(1, 0.0, 0.0, 0.0, "A", 0x111111)
//    val nodeB = makeNode(2, 1.0, 1.0, 1.0, "B", 0x222222)
//    val nodeC = makeNode(3, 2.0, 2.0, 2.0, "C", 0x333333)
//    GraphState.commandObserver.onNext(SetNodes(Set(nodeA, nodeB, nodeC)))
//
//    // Now set edges by IDs
//    val cmd = SetEdgesByIds(Set((1, 2), (1, 3)))
//    GraphState.commandObserver.onNext(cmd)
//
//    val expectedEdges = Set(
//      Edge((nodeA, nodeB)),
//      Edge((nodeA, nodeC))
//    )
//
//    assertEquals(
//      GraphState.edges.signal.now(),
//      expectedEdges,
//      "GraphState.edges should be replaced with edges computed from existing nodes"
//    )
//  }
