# Organizzazione del codice

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

- **`domain`**: Rappresenta il cuore della logica applicativa, modellando i concetti fondamentali del sistema.

- **`state`**: Si occupa della gestione dello stato dell'applicazione e delle sue transizioni. Questo package suggerisce l'uso di un approccio reattivo, dove lo stato è centrale e gli aggiornamenti vengono propagati automaticamente.

- **`view`**: Si concentra sulla presentazione e l'interazione con l'utente. La suddivisione in sottopackage rende evidente l'uso del **Component-Based Design**, dove ogni componente ha una responsabilità specifica. Questo favorisce il riuso e la manutenibilità del codice.

- **`api`**: Definisce le interfacce e i parser per comunicare con l'esterno.

## Dettagli del package `view`

Il package `view` è ulteriormente suddiviso in:

- **`components`**: Contiene componenti grafici riutilizzabili, seguendo un approccio modulare.

- **`config`**: Gestisce le configurazioni necessarie per personalizzare e parametrizzare l'applicazione.

- **`player`**: Include il player per gestire l'esecuzione del grafo, con funzionalità di *Play*, *Pausa* e regolazione della velocità.

- **`graph`**: Contiene il visualizzatore 3D del grafo.

## Dominio

Il dominio è il cuore dell'applicazione, dove sono definiti i concetti fondamentali. È formato da due concetti: Le entità e i comandi ad esse associate. Le entità sono rappresentate da nodi e archi, mentre i comandi sono azioni che possono essere eseguite sul grafo.

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

Per quanto riguarda il dominio dell'animazione, sono definiti i comandi per controllare l'animazione, come l'avvio, la pausa, l'avanzamento di un singolo passo o di più passi alla volta, il reset e il cambio di modalità di visualizzazione.

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

È importante notare come sia importante che tutto ciò che si trova in questo package non abbia dipendenze esterne, in modo da poter essere facilmente testato e riutilizzato.

## State

Il package `state` si occupa di gestire lo stato dell'app andando a definire le strutture dati e le interfacce necessarie per mantenere lo stato dell'applicazione. Inoltre, definisce le interfacce per la gestione dei comandi e degli eventi. Lo stato dell'applicazione è totalmente reattivo, in modo da garantire una gestione efficiente e coerente degli aggiornamenti. Lo stato è modificabile solo tramite comandi.

```mermaid
classDiagram
    class GraphState {
        <<trait>>
        +nodes: StrictSignal[Set[GraphNode]]
        +edges: StrictSignal[Set[GraphEdge]]
        +commandObserver: Observer[GraphCommand]
    }

    class GraphStateObject {
        <<object>>
        -nodesVar: Var[Set[GraphNode]]
        -edgesVar: Var[Set[GraphEdge]]
        +nodes: StrictSignal[Set[GraphNode]]
        +edges: StrictSignal[Set[GraphEdge]]
        +commandObserver: Observer[GraphCommand]
    }

    GraphStateObject --> GraphState : extends
```

```mermaid
classDiagram
    class AnimationState {
        <<trait>>
        +running: StrictSignal[Boolean]
        +engine: StrictSignal[Option[js.Dynamic]]
        ...
        +animationObserver: Observer[AnimationCommand[js.Dynamic]]
    }

    class AnimationStateObject {
        <<object>>
        -runningVar: Var[Boolean]
        -engineVar: Var[Option[js.Dynamic]]
        +running: StrictSignal[Boolean]
        +engine: StrictSignal[Option[js.Dynamic]]
        ...
        +animationObserver: Observer[AnimationCommand[js.Dynamic]]
    }

    AnimationStateObject --> AnimationState : extends
```

## Api

Il package `api` definisce le interfacce e i parser necessari per comunicare con l'esterno. In particolare, definisce le interfacce per l'aggiunta di nodi e archi, e i relativi parser per convertire i dati in input in strutture dati utilizzabili dall'applicazione.

```mermaid
classDiagram
    direction TB

    class Parser~T, A~ {
        <<trait>>
        +parse(t: T): Option[Set[A]]
    }

    class NodeParser {
        <<object>>
        +parse(jsonString: String): Option[Set[GraphNode]]
    }

    class EdgeParser {
        <<object>>
        +parse(jsonString: String): Option[Set[(Id, Id)]]
    }

    class GraphApiError {
        <<sealed trait>>
        +message: String
    }

    class ParsingError {
        +message: String
    }

    class GraphAPIService {
        <<trait>>
        +addNodesFromJson(input: String): Option[GraphApiError]
        +addEdgesFromJson(input: String): Option[GraphApiError]
    }

    class GraphAPI {
        <<object>>
        +addNodesFromJson(input: String): Option[GraphApiError]
        +addEdgesFromJson(input: String): Option[GraphApiError]
    }

    Parser~T, A~ <|-- NodeParser
    Parser~T, A~ <|-- EdgeParser

    GraphApiError <|-- ParsingError

    GraphAPI --> GraphAPIService
    GraphAPI --> GraphApiError
    GraphAPI --> NodeParser
    GraphAPI --> EdgeParser
```

## View

La view è gestita per mezzo di componenti grafici riutilizzabili, seguendo un approccio modulare. Ogni componente ha una responsabilità specifica, favorendo il riuso e la manutenibilità del codice.

```mermaid
classDiagram
    class MainView {
        + render() Unit
    }

    class ThreeScene {
        + renderScene(canvasId: String): HtmlElement
        + setNodes(nodes: Nodes): Unit
        + setEdges(edges: Edges): Unit
        + centerView(): Unit
    }

    class GridViewControllerView {
        + render: HtmlElement
    }

    class EngineSettingsControllerView {
        + render: HtmlElement
    }

    class AnimationControllerView {
        + render: HtmlElement
    }

    class ViewComponent {
        + render: Element
    }



    MainView --> ThreeScene : scene
    MainView --> GridViewControllerView : sceneController
    MainView --> EngineSettingsControllerView : engineSettings
    MainView --> AnimationControllerView : animationController

    
    ViewComponent <|-- GridViewControllerView
    ViewComponent <|-- EngineSettingsControllerView
    ViewComponent <|-- AnimationControllerView
```

### Graph

Il package `graph` si occupa di visualizzare il grafo in 3D. In particolare, definisce le interfacce per la gestione dei nodi e degli archi, e per la visualizzazione del grafo. È stato usato il pattern **Adapter** per adattare le interfacce del dominio a quelle della libreria Three.js. In questo modo è stato possibile separare la logica dell'applicazione dalla libreria grafica.

```mermaid
classDiagram
    class ThreeScene {
        - SceneState state
        - SceneWrapper sceneWrapper

        + setNodes(newNodes: Set[GraphNode]): Unit
        + setEdges(newEdges: Set[GraphEdge]): Unit
        + renderScene(elementId: String): Element
    }

    class SceneWrapper {
        + underlying: Scene
        + addObject(obj: Object3D): Unit
        + removeObject(obj: Object3D): Unit
    }


    class OrbitControls {
        + update(): Unit
    }

    class SceneConfig {
        + fov: Double
        + width: Int
        + height: Int
        + near: Double
        + far: Double
    }

    ThreeScene --> SceneWrapper : uses
    ThreeScene --> SceneConfig : configures
    ThreeScene --> OrbitControls : initializes

```
