# Implementazione

## Domanin
    
```scala 3
sealed trait GraphType:
  type Id    = Int
  type Color = Int
  type Label = String

object GraphDomain extends GraphType:
  final case class Position(x: Double, y: Double, z: Double)
  final case class GraphNode(
      id: Id,
      position: Position,
      label: Label,
      color: Color
  )
  final case class GraphEdge(nodes: (GraphNode, GraphNode)):
    override def equals(obj: Any): Boolean = obj match
      case that: GraphEdge =>
        this.nodes == that.nodes || this.nodes == that.nodes.swap
      case _ => false

  sealed trait GraphCommand
  case class SetNodes(nodes: Set[GraphNode])        extends GraphCommand
  case class SetEdges(edges: Set[GraphEdge])        extends GraphCommand
  case class SetEdgesByIds(edgesIds: Set[(Id, Id)]) extends GraphCommand

object AnimationDomain:
  enum ViewMode:
    case Mode2D, Mode3D
  sealed trait AnimationCommand[Engine]
  case class SetEngine[Engine](engine: Engine)  extends AnimationCommand[Engine]
  case class StartAnimation[Engine]()           extends AnimationCommand[Engine]
```

## State

```scala 3
trait GraphState:
  val nodes: StrictSignal[Set[GraphNode]]
  val edges: StrictSignal[Set[GraphEdge]]
  val commandObserver: Observer[GraphCommand]

object GraphState extends GraphState:
  private val nodesVar: Var[Set[GraphNode]]        = Var(Set.empty[GraphNode])
  private val edgesVar: Var[Set[GraphEdge]]        = Var(Set.empty[GraphEdge])
  override val nodes: StrictSignal[Set[GraphNode]] = nodesVar.signal
  override val edges: StrictSignal[Set[GraphEdge]] = edgesVar.signal

  override val commandObserver: Observer[GraphCommand] =
    Observer[GraphCommand] {

      case SetNodes(newNodes) =>
        nodesVar.set(newNodes)
    
    }

```

## Engine Loop
    
```scala 3
  override def start(): Unit =
    def loop(): Unit =
      if running.now() then
        val batchCount = batch.now()
        for _ <- 1 to batchCount do getEngineOrEmpty.executeIterations()
        animationObserver.onNext(NextTickAdd(batchCount + 1))
        handleNewData(processNextBatch())
        setTimeout(() => loop(), 8)
    loop()
```

## Extension

```scala 3
object DomainExtensions:
  extension (edge: GraphEdge)
    def object3dName: String =
      val (n1, n2) = edge.nodes
      val (minId, maxId) =
        if n1.id < n2.id then (n1.id, n2.id) else (n2.id, n1.id)
      s"edge-$minId-$maxId-$n1-$n2"

  extension (node: GraphNode)
    def object3dName: String = s"node-${node.id}"
```

## Three.js Types and Adapter

```scala 3
type GenericObject3D = Object3D[Object3DEventMap]
type ThreeGroup      = Group[Object3DEventMap]
type ThreePoints =
  Points[BufferGeometry[Nothing], PointsMaterial, Object3DEventMap]
type ThreeSprite = Sprite[Object3DEventMap]
type ThreeLine =
  Line[BufferGeometry[Nothing], LineBasicMaterial, Object3DEventMap]
type ThreeMesh   = Mesh[BufferGeometry[Nothing], Material, Object3DEventMap]
type ThreeCamera = Camera

object ThreeType:

  def unsafeCast[T](obj: js.Any): T = obj.asInstanceOf[T]
  
  object GenericObject3D:
    def unapply(obj: Object3D[?]): Option[GenericObject3D] =
      Some(obj.asInstanceOf[GenericObject3D])
      
  object ThreeCamera:
    def unapply(cam: PerspectiveCamera): Option[ThreeCamera] = cam.`type` match
      case "PerspectiveCamera" => Some(cam.asInstanceOf[ThreeCamera])
      case "Camera"            => Some(cam.asInstanceOf[ThreeCamera])
      case _                   => None

  object Group:
    def unapply(obj: GenericObject3D): Option[ThreeGroup] = obj.`type` match
      case "Group" => Some(obj.asInstanceOf[ThreeGroup])
      case _       => None

  object Line:
    def unapply(obj: GenericObject3D): Option[ThreeLine] = obj.`type` match
      case "Line" => Some(obj.asInstanceOf[ThreeLine])
      case _      => None
```

```scala 3
def removeObject(obj: GenericObject3D): Unit =
      import ThreeType._
      obj match
        case Group(group) =>
          for
            child <- group.children
            _ <- child match
              case Line(line) =>
                line.geometry.dispose()
                line.material.dispose()
                underlying.remove(line)
              case Points(points) =>
                points.geometry.dispose()
                points.material.dispose()
                underlying.remove(points)
              case Sprite(sprite) =>
                sprite.geometry.dispose()
                sprite.material.dispose()
                sprite.material.map.dispose()
                underlying.remove(sprite)
              case _ => ()
          do underlying.remove(group)
        case _ => ()
```

## Laminar View

```scala 3
def render(): Unit =
    val rootElement = div(
      scene.renderScene("three_canvas"),
      sceneController.render,
      animationController.render,
      engineSettings.render,
      running --> {
        case true => player.start()
        case _    => ()
      },
      engine --> {
        case Some(engine) => player.loadNextFrame()
        case _            => ()
      },
      edges.combineWith(nodes) --> {
        case (edges, nodes) =>
          scene.setNodes(nodes)
          scene.setEdges(edges)
      },
      onMountCallback(_ => initialize())
    )
```

## Engine Import From JS
    
```scala 3
val engine = js.Dynamic.global.EngineImpl(
  xVar.now(),
  yVar.now(),
  zVar.now(),
  distXVar.now(),
  distYVar.now(),
  distZVar.now(),
  edgeDistVar.now()
)
```