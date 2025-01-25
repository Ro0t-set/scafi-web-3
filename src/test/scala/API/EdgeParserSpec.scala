package API

import munit.FunSuite

class EdgeParserSpec extends FunSuite:

  test("parse valid JSON with multiple Edges") {
    val jsonString =
      """
        |[
        |  {
        |    "source": 1,
        |    "target": 2
        |  },
        |  {
        |    "source": 3,
        |    "target": 4
        |  }
        |]
      """.stripMargin

    val resultOpt = EdgeParser.parse(jsonString)

    resultOpt.fold(
      fail("Expected Some(Set[(Id, Id)]) but got None")
    ) { resultSet =>
      assertEquals(
        resultSet.size,
        2,
        "We should have exactly two Edges in the result"
      )

      assert(resultSet.contains((1, 2)), "Result should contain edge (1, 2)")
      assert(resultSet.contains((3, 4)), "Result should contain edge (3, 4)")
    }
  }

  test("parse valid JSON with single Edge") {
    val jsonString =
      """
        |[
        |  {
        |    "source": 100,
        |    "target": 200
        |  }
        |]
      """.stripMargin

    EdgeParser.parse(jsonString).fold(
      fail("Expected Some(Set[(Id, Id)]) but got None")
    ) { edgeSet =>
      assertEquals(
        edgeSet.size,
        1,
        "We should have exactly one Edge in the result"
      )

      val parsedEdge =
        edgeSet.headOption.getOrElse(fail("Expected an Edge in the Set"))
      assertEquals(parsedEdge._1, 100)
      assertEquals(parsedEdge._2, 200)
    }
  }

  test("parse returns None for invalid JSON") {
    val invalidJsonString =
      """{ "some": "bad", "json": [ }"""
    val resultOpt = EdgeParser.parse(invalidJsonString)

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
        |    "source": 1
        |  }
        |]
      """.stripMargin

    val resultOpt = EdgeParser.parse(missingFieldsJson)

    assert(
      resultOpt.isEmpty,
      s"Expected None because 'target' field is missing, but got $resultOpt"
    )
  }
