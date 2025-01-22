package features
import io.cucumber.scala.EN
import io.cucumber.scala.ScalaDsl

import scala.sys.process._

class UnitTestWrapper extends ScalaDsl with EN:

  Then("The unit tests called {string} should pass") { (testName: String) =>
    val command   = s"sbt \"testOnly $testName\""
    val exitValue = command.!
    assert(exitValue == 0, "Test execution failed")
  }
