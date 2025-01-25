package API

import munit.FunSuite
import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll

class EdgeParserSpec extends FunSuite with ScalaCheckSuite:

  def validEdgeJsonGen: Gen[String] =
    for
      source <- Gen.choose(1, 1000)
      target <- Gen.choose(1, 1000)
    yield s"""[{
      "source": $source,
      "target": $target
      }]"""

  test("EdgeParser parses valid single edge JSON") {
    forAll(validEdgeJsonGen) { jsonString =>
      EdgeParser.parse(jsonString).fold(false) { edges =>
        edges.size == 1
      }
    }
  }

  test("EdgeParser handles invalid JSON") {
    forAll(Gen.numStr) { invalidJson =>
      EdgeParser.parse(invalidJson).isEmpty
    }
  }
