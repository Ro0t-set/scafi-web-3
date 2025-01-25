
# Requisiti

## Buisness

L'applicazione deve permettere di caricare un programma aggregato, visualizzarlo in un grafo 3D e permettere di eseguirlo. La compilazione del programma deve avvenire runtime, utilizzando un server esterno.

## Modello di dominio

```mermaid
classDiagram
    %% =======================
    %% Graph Domain Model
    %% =======================

    class GraphType {
      <<sealed trait>>
      + type Id = Int
      + type Color = Int
      + type Label = String
    }

    class GraphDomain {
      <<object>>
    }

    GraphDomain --> GraphType : extends

    class Position {
      <<final case class>>
      - Double x
      - Double y
      - Double z
    }

    class GraphNode {
      <<final case class>>
      - Id id
      - Position position
      - Label label
      - Color color
    }

    class GraphEdge {
      <<final case class>>
      - (GraphNode, GraphNode) nodes
    }

    class GraphCommand {
      <<sealed trait>>
    }


    %% Relationships
    GraphEdge --> GraphNode  nodes: (GraphNode, GraphNode)

    %% For clarity: show that GraphDomain "contains" these case classes
    GraphDomain --> Position
    GraphDomain --> GraphNode
    GraphDomain --> GraphEdge
    GraphDomain --> GraphCommand

    %% =======================
    %% Animation Domain Model
    %% =======================

    class AnimationDomain {
      <<object>>
    }

    class ViewMode {
      <<enum>>
      Mode2D
      Mode3D
    }

    class AnimationCommand~Engine~ {
      <<sealed trait>>
    }


    %% Relationships
    AnimationDomain --> ViewMode : of type
    AnimationDomain --> AnimationCommand

```

## Funzionali

### Utente

1. Caricamento di un programma aggregato.
2. Visualizzazione del programma aggregato in un grafo 3D.
3. Poter controllare l'esecuzione del programma aggregato.
4. Decidere le dimensioni del grafo.

### Sistema

1. Utilizzo di *Scastie* per la compilazione del programma aggregato.
2. Possibilità di caricare codice direttamente dal proprio account *Scastie*.
3. Visualizzatore 3D del grafo.
4. Player per la gestione del grafo con funzioni di *Play*, *Pausa* e regolazione della velocità.
5. Possibilità di modificare le dimensioni del grafo.
6. Inclusione di vari esempi di programmi aggregati (da aggiungere).
7. Utilizzo di *Cucumber* per i test, integrato con *Selenium* per testare la grafica.
8. CI/CD per test e deploy.
9. Il grafo supporta più di 30 aggiornamenti al secondo.

## Non funzionali

1. Affidabilità: l'applicazione deve essere stabile, evitando crash.
2. Documentazione: l'intero progetto deve essere ben documentato, in modo da facilitare la comprensione del codice.
3. Performance: l'applicazione deve essere veloce e reattiva, evitando prolungate attese per l'utente nello svolgimento delle azioni.
4. Portabilità: l'applicazione deve essere disponibile su più piattaforme.
5. Manutenibilità: il codice deve essere ben strutturato e facilmente manutenibile.

## Requisiti Opzionali

1. Possibilità di centrare il grafo.
2. Possibilità di cambiare la grafica del grafo.
3. Nascondere il boilerplate Scastie.
4. Cambiare la visualizzazione da 3D a 2D.

## Implementazione

Utilizzo di:

### Ambiente

- Scala 3.3.4
- ScalaJS 1.18.x
- JDK 21+

### Librerie Principali

- Laminar 1.17.x

### Testing

- Cucumber 8.25.x
- MUnit 1.0.x
- Selenium 4.1.x

### Build

- Scastie (embedded)

### Formattazione e Qualità

- Scalafmt 2.5.x
- Scalafix 0.14.x
- sbt-wartremover 3.2.x

### CI/CD

- GitHub Actions

### Documentazione e deployment

- vite 6.x.x
- vitepress 1.x.x
