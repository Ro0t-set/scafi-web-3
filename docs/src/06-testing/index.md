# Testing

Durante le prime fasi di sviluppo del progetto, i test non sono stati sviluppati, poiché l'obiettivo principale era creare un prototipo funzionante. Tuttavia, per garantire la correttezza delle funzionalità implementate, una volta ottenuto il prototipo, è stato adottato fin da subito un approccio **Test-Driven Development (TDD)** utilizzando [MUnit](https://scalameta.org/munit/).Successivamente, raggiunta una versione stabile del progetto, si è adottato l'approccio **Behavior-Driven Development (BDD)** impiegando [Cucumber](https://cucumber.io/) integrato con [Selenium](https://www.selenium.dev/).

## MUnit

```scala
```

Di seguito un esempio di test usando MUnit:

## Cucumber

```gherkin
    Feature: Unit Test Feature
    @unit
    Scenario Outline: Unit Test Scenario
        Then The unit tests called "<testName>" should pass
        Examples:
        | testName                 |
        | state.AnimationStateSpec |
        | state.GraphStateSpec     |
        | API.NodeParserSpec       |
        | API.EdgeParserSpec       |
```

```scala
class UnitTestWrapper extends ScalaDsl with EN:
  Then("The unit tests called {string} should pass") { (testName: String) =>
    val command   = s"sbt \"testOnly $testName\""
    val exitValue = command.!
    assert(exitValue == 0, "Test execution failed")
  }
```

```gherkin
    Feature: Unit Test Feature
    @unit
    Scenario Outline: Unit Test Scenario
        Then The unit tests called "<testName>" should pass
        Examples:
        | testName                 |
        | state.AnimationStateSpec |
        | state.GraphStateSpec     |
        | API.NodeParserSpec       |
        | API.EdgeParserSpec       |
```

```gherkin
    @web
    Scenario: High Update Rate Support
        Given I am on the Scafi Web Page
        Then the engine "EngineImpl" is loaded
        Then the graph 10x10x2 should support more than "30" updates per second
```
