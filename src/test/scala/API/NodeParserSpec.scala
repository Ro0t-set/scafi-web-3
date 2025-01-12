package API

import munit.FunSuite
import domain.{Node, Position}

class NodeParserSpec extends FunSuite:

  test("parse valid JSON with multiple Nodes") {
    val jsonString =
      """
        |[
        |  {
        |    "id": 1,
        |    "label": "Node1",
        |    "color": 111111,
        |    "position": {
        |      "x": 1.1,
        |      "y": 2.2,
        |      "z": 3.3
        |    }
        |  },
        |  {
        |    "id": 2,
        |    "label": "Node2",
        |    "color": 222222,
        |    "position": {
        |      "x": 4.4,
        |      "y": 5.5,
        |      "z": 6.6
        |    }
        |  }
        |]
      """.stripMargin

    val resultOpt = NodeParser.parse(jsonString)

    // Use `fold` to handle None vs Some
    resultOpt.fold(
      fail("Expected Some(Set[Node]) but got None") // If None
    ) { resultSet =>                                // If Some(resultSet)
      assertEquals(
        resultSet.size,
        2,
        "We should have exactly two Nodes in the result"
      )

      val node1 = Node(
        id = 1,
        label = "Node1",
        color = 111111,
        position = Position(1.1, 2.2, 3.3)
      )
      val node2 = Node(
        id = 2,
        label = "Node2",
        color = 222222,
        position = Position(4.4, 5.5, 6.6)
      )

      assert(resultSet.contains(node1), "Result should contain node1")
      assert(resultSet.contains(node2), "Result should contain node2")
    }
  }

  test("parse valid JSON with single Node") {

    val jsonString =
      """
        |[
        |  {
        |    "id": 100,
        |    "label": "SingleNode",
        |    "color": 999999,
        |    "position": {
        |      "x": 10.0,
        |      "y": 20.0,
        |      "z": 30.0
        |    }
        |  }
        |]
      """.stripMargin

    NodeParser.parse(jsonString).fold(
      fail("Expected Some(Set[Node]) but got None")
    ) { nodeSet =>
      assertEquals(
        nodeSet.size,
        1,
        "We should have exactly one Node in the result"
      )

      val parsedNode =
        nodeSet.headOption.getOrElse(fail("Expected a Node in the Set"))
      assertEquals(parsedNode.id, 100)
      assertEquals(parsedNode.label, "SingleNode")
      assertEquals(parsedNode.color, 999999)
      assertEquals(parsedNode.position, Position(10.0, 20.0, 30.0))
    }
  }

  test("parse returns None for invalid JSON") {
    val invalidJsonString =
      """{ "some": "bad", "json": [ }""" // Malformed on purpose
    val resultOpt = NodeParser.parse(invalidJsonString)

    // Just verify we have None:
    assert(
      resultOpt.isEmpty,
      s"Expected None, but got $resultOpt"
    )
  }

  test("parse returns None for valid JSON, but missing fields") {
    val missingFieldsJson =
      """
        |[
        |  {
        |    "label": "NoID",
        |    "color": 111111,
        |    "position": {
        |      "x": 1.1,
        |      "y": 2.2,
        |      "z": 3.3
        |    }
        |  }
        |]
      """.stripMargin

    val resultOpt = NodeParser.parse(missingFieldsJson)

    assert(
      resultOpt.isEmpty,
      s"Expected None because 'id' field is missing, but got $resultOpt"
    )
  }
