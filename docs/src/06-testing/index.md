# Testing

Durante le prime fasi di sviluppo del progetto, non sono stati sviluppati test, poiché l'obiettivo era quello di creare un prototipo funzionante. Tuttavia, per garantire la correttezza delle funzionalità sviluppate, una volta ottenuto un prototipo funzionate è stato fin da subito adottato un approccio Test-Driven Development (TDD) utilizzando [MUnit](https://scalameta.org/munit/). Ua volta ottenuta una versione stabile del progetto, è stato adottato l'approccio Behavior Driven Development (BDD) utilizzando [Cucumber](https://cucumber.io/) integrato con [Selenium](https://www.selenium.dev/).

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
