# Testing

Durante le prime fasi di sviluppo del progetto, i test non sono stati sviluppati, poiché l'obiettivo principale era creare un prototipo funzionante. Tuttavia, per garantire la correttezza delle funzionalità implementate, una volta ottenuto il prototipo, è stato adottato fin da subito un approccio **Test-Driven Development (TDD)** utilizzando [MUnit](https://scalameta.org/munit/) assieme a [ScalaCheck](https://scalameta.org/munit/docs/integrations/scalacheck.html).Successivamente, raggiunta una versione stabile del progetto, si è adottato l'approccio **Behavior-Driven Development (BDD)** impiegando [Cucumber](https://cucumber.io/) integrato con [Selenium](https://www.selenium.dev/).

## MUnit

Di seguito un esempio di usando MUnit e ScalaCheck:

```scala
  test("addNode should add a node to the state") {
    forAll {
      (id: Int, label: String, color: Int, x: Double, y: Double, z: Double) =>
        val node: Set[GraphNode] =
          Set(GraphNode(id, Position(x, y, z), label, color))
        GraphState.commandObserver.onNext(SetNodes(node))
        val result: Set[GraphNode] = GraphState.nodes.now()
        result == node
    }
  }
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

L'idea alla base del codice presentato consiste nel creare uno step di Cucumber che esegua i test specificati in testName e verifichi che il loro valore di uscita sia pari a 0, indicando che i test sono stati superati con successo. Questo approccio mira a fornire una conferma chiara al cliente che i test siano passati e che il codice sia funzionante. Utilizzare direttamente Cucumber per testare il codice è un'idea che potrebbe essere valutata in futuro, ma attualmente presenta alcune problematiche che ne riducono l'efficienza. Nello specifico, questo metodo introduce diverse viscosità e rende il processo di sviluppo tramite TDD significativamente più lento. Per queste ragioni, si è preferito adottare l'approccio descritto sopra, che permette una verifica più rapida e diretta del codice.

```gherkin
    @web
    Scenario: High Update Rate Support
        Given I am on the Scafi Web Page
        Then the engine "EngineImpl" is loaded
        Then the graph 10x10x2 should support more than "30" updates per second
```

D'altra parte l'approccio BDD è stato adottato per testare l'interfaccia grafica e le funzionalità dell'applicazione. In questo caso, Cucumber è stato utilizzato per definire i test e Selenium per simulare le interazioni utente. Questo approccio è stato scelto per garantire che l'applicazione soddisfi i requisiti funzionali e che le funzionalità siano implementate correttamente.
