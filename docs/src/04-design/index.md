# Design

## Organizzazione del codice

Il codice è organizzato in quattro package principali:

```mermaid
    flowchart TD
        B[domain] --> A[ScafiWeb3]
        C[state] --> A
        D[view] --> A
        E[api] --> A

        F[components] --> D
        G[config] --> D
        H[player] --> D
        I[graph] --> D

        J[adapter] --> I
        K[component] --> I
        L[config] --> I
        M[extensions] --> I
        N[scene] --> I
```

- `domain`: contiene la logica e i concetti fondamentali del sistema
- `state`: si occupa di gestire lo stato dell'applicazione e le sue transizioni
- `view`: gestisce la presentazione dei dati e l'interazione con l'utente
- `api`: contiene le interfacce ed il parser per comunicare con Scastie

A sua volta il package `view` è suddiviso in:

- `components`: contiene componenti grafici riutilizzabili
- `config` : contiene le configurazioni dell'applicazione
- `player`: contiene il player per la gestione del grafo con funzioni di *Play*, *Pausa* e regolazione della velocità ed il controller per interagire con scastie
- `graph`: contiene il visualizzatore 3D del grafo

## Domanin

```mermaid
classDiagram
    class GraphType {
        <<trait>>
        +type Id : Int
        +type Color : Int
        +type Label : String
    }

    class GraphDomain {
        <<object>>
    }

    GraphDomain --> GraphType : extends

    class Position {
        +x : Double
        +y : Double
        +z : Double
    }

    class GraphNode {
        +id : Id
        +position : Position
        +label : Label
        +color : Color
    }

    class GraphEdge {
        +nodes : (GraphNode, GraphNode)
        +equals(obj: Any) : Boolean
    }

    GraphDomain --> Position
    GraphDomain --> GraphNode
    GraphDomain --> GraphEdge

    class GraphCommand {
        <<sealed trait>>
    }

    class SetNodes {
        +nodes : Set[GraphNode]
    }

    class SetEdges {
        +edges : Set[GraphEdge]
    }

    class SetEdgesByIds {
        +edgesIds : Set[(Id, Id)]
    }

    GraphCommand <|-- SetNodes
    GraphCommand <|-- SetEdges
    GraphCommand <|-- SetEdgesByIds
    GraphDomain --> GraphCommand
```
```mermaid
classDiagram

    class AnimationDomain {
        <<object>>
    }

    class ViewMode {
        <<enum>>
        +Mode2D
        +Mode3D
    }

    AnimationDomain --> ViewMode

    class AnimationCommand~Engine~ {
        <<sealed trait>>
    }

    class SetEngine~Engine~ {
        +engine : Engine
    }

    class StartAnimation~Engine~
    class PauseAnimation~Engine~
    class NextTick~Engine~
    class NextTickAdd~Engine~ {
        +tick : Int
    }
    class AnimationBatch~Engine~ {
        +batch : Int
    }
    class Reset~Engine~
    class SwitchMode~Engine~

    AnimationCommand~Engine~ <|-- SetEngine~Engine~
    AnimationCommand~Engine~ <|-- StartAnimation~Engine~
    AnimationCommand~Engine~ <|-- PauseAnimation~Engine~
    AnimationCommand~Engine~ <|-- NextTick~Engine~
    AnimationCommand~Engine~ <|-- NextTickAdd~Engine~
    AnimationCommand~Engine~ <|-- AnimationBatch~Engine~
    AnimationCommand~Engine~ <|-- Reset~Engine~
    AnimationCommand~Engine~ <|-- SwitchMode~Engine~

    AnimationDomain --> AnimationCommand~Engine~
```


## State

## View

### Graph

## Api
