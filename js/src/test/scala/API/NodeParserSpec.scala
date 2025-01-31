package API

import domain.GraphDomain.GraphNode
import domain.GraphDomain.Position
import munit.FunSuite
import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop
import org.scalacheck.Prop.forAll

class NodeParserSpec extends FunSuite with ScalaCheckSuite:

  def validNodeJsonGen: Gen[(String, GraphNode)] =
    for
      id    <- Gen.choose(1, 1000)
      label <- Gen.alphaStr
      color <- Gen.choose(0, Int.MaxValue)
      x     <- Gen.choose(-1000.0, 1000.0)
      y     <- Gen.choose(-1000.0, 1000.0)
      z     <- Gen.choose(-1000.0, 1000.0)
    yield (
      s"""[{
      "id": $id,
      "label": "$label",
      "color": $color,
      "position": {
        "x": $x,
        "y": $y,
        "z": $z
      }
    }]""",
      GraphNode(id, Position(x, y, z), label, color)
    )

  test("NodeParser parses valid single node JSON") {
    forAll(validNodeJsonGen) {
      case (jsonString, validJsonNodeFromParams) =>
        NodeParser.parse(jsonString).fold(false) { nodes =>
          nodes == Set(validJsonNodeFromParams)
        }
    }
  }

  test("NodeParser handles invalid JSON") {
    forAll(Gen.numStr) { invalidJson =>
      NodeParser.parse(invalidJson).isEmpty
    }
  }
