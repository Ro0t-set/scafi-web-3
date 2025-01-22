package features
import io.cucumber.scala.EN
import io.cucumber.scala.ScalaDsl

import scala.sys.process._

class NodeParserWrapper extends ScalaDsl with EN:
  Then("Must Parse Correctly a set of nodes") {
    val command   = "sbt \"testOnly API.NodeParserSpec\""
    val exitValue = command.!
    assert(exitValue == 0, "Test execution failed")
  }
