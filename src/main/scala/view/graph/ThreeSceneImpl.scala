package view.graph

import com.raquo.laminar.api.L.*
import domain.{Edge, Node, Position}
import org.scalajs.dom
import org.scalajs.dom.window.requestAnimationFrame
import typings.three.examplesJsmControlsOrbitControlsMod.OrbitControls
import typings.three.mod.*
import view.adapter.SceneWrapper
import view.adapter.ThreeJsAdapter.*
import view.graph.component.{Edge3D, Node3D}

/** Extension methods for domain objects */
object DomainExtensions:
  extension (edge: Edge)
    def canonicalEdgeName: String =
      val (n1, n2) = edge.nodes
      val (minId, maxId) =
        if n1.id < n2.id then (n1.id, n2.id) else (n2.id, n1.id)
      s"edge-$minId-$maxId"

  extension (node: Node)
    def object3dName: String = s"node-${node.id}"

/** Configuration for Three.js scene */
case class SceneConfig(
    width: Int,
    height: Int,
    zPointOfView: Int,
    fov: Int = 75,
    near: Double = 0.1,
    far: Double = 1600
)

/** Represents a Three.js scene with nodes and edges */
trait ThreeScene:
  def setNodes(newNodes: Set[Node]): Unit
  def setEdges(newEdges: Set[Edge]): Unit
  def renderScene(elementId: String): Element

/** Implementation of ThreeScene using Three.js */
@SuppressWarnings(Array("org.wartremover.warts.All"))
final class ThreeSceneImpl private (config: SceneConfig) extends ThreeScene:
  import DomainExtensions.*

  private case class SceneState(
      currentNodes: Set[Node] = Set.empty,
      currentEdges: Set[Edge] = Set.empty,
      nodeObjects: Map[String, Object3DType] = Map.empty,
      edgeObjects: Map[String, Object3DType] = Map.empty
  )

  private var state = SceneState()

  private val sceneWrapper = SceneWrapper()
  private val scene        = sceneWrapper.underlying

  private val camera   = initCamera(config)
  private val renderer = initRenderer(config)
  private val controls = initControls(config)

  private def initCamera(config: SceneConfig): PerspectiveCamera =
    val camera = CameraFactory.createPerspectiveCamera(
      fov = config.fov,
      aspect = config.width.toDouble / config.height.toDouble,
      near = config.near,
      far = config.far
    )
    VectorUtils.setPosition(
      camera,
      config.width / 2.0,
      config.height / 2.0,
      config.zPointOfView
    )
    camera

  private def initRenderer(config: SceneConfig): WebGLRenderer =
    val renderer = RendererFactory.createWebGLRenderer()
    renderer.setSize(config.width, config.height)
    renderer

  private def initControls(config: SceneConfig): OrbitControls =
    val controls = ControlsFactory.createOrbitControls(
      camera = camera,
      domElement = renderer.domElement
    )
    controls.enableZoom = true
    controls.enablePan = true
    controls.enableRotate = true
    VectorUtils.setTarget(controls, config.width / 2.0, config.height / 2.0, 0)
    controls.update()
    controls

  override def setNodes(newNodes: Set[Node]): Unit =
    val (nodesToAdd, nodesToRemove) = calculateNodeDiff(newNodes)

    removeNodes(nodesToRemove)
    addNodes(nodesToAdd)

    state = state.copy(currentNodes = newNodes)

  override def setEdges(newEdges: Set[Edge]): Unit =
    val (edgesToAdd, edgesToRemove) = calculateEdgeDiff(newEdges)

    removeEdges(edgesToRemove)
    addEdges(edgesToAdd)

    state = state.copy(currentEdges = newEdges)

  private def calculateNodeDiff(newNodes: Set[Node]): (Set[Node], Set[Node]) =
    val nodesToAdd    = newNodes.diff(state.currentNodes)
    val nodesToRemove = state.currentNodes.diff(newNodes)
    (nodesToAdd, nodesToRemove)

  private def calculateEdgeDiff(newEdges: Set[Edge]): (Set[Edge], Set[Edge]) =
    val edgesToAdd    = newEdges.diff(state.currentEdges)
    val edgesToRemove = state.currentEdges.diff(newEdges)
    (edgesToAdd, edgesToRemove)

  private def removeNodes(nodesToRemove: Set[Node]): Unit =
    nodesToRemove.foreach { oldNode =>
      state.nodeObjects.get(oldNode.object3dName).foreach(
        sceneWrapper.removeObject
      )
      state = state.copy(nodeObjects = state.nodeObjects - oldNode.object3dName)
    }

  private def addNodes(nodesToAdd: Set[Node]): Unit =
    nodesToAdd.foreach { node =>
      val node3D = createNode3D(node)
      state = state.copy(nodeObjects =
        state.nodeObjects + (node.object3dName -> node3D)
      )
      sceneWrapper.addObject(node3D)
    }

  private def createNode3D(node: Node): Object3DType =
    Node3D(
      id = node.id.toString,
      textLabel = node.label,
      x = node.position.x,
      y = node.position.y,
      z = node.position.z,
      nodeColor = node.color,
      name = node.object3dName
    )

  private def removeEdges(edgesToRemove: Set[Edge]): Unit =
    edgesToRemove.foreach { oldEdge =>
      val edgeName = oldEdge.canonicalEdgeName
      state.edgeObjects.get(edgeName).foreach(sceneWrapper.removeObject)
      state = state.copy(edgeObjects = state.edgeObjects - edgeName)
    }

  private def addEdges(edgesToAdd: Set[Edge]): Unit =
    edgesToAdd.foreach { edge =>
      val edgeName = edge.canonicalEdgeName
      if !state.edgeObjects.contains(edgeName) then
        val edge3D = createEdge3D(edge)
        state =
          state.copy(edgeObjects = state.edgeObjects + (edgeName -> edge3D))
        sceneWrapper.addObject(edge3D)
    }

  private def createEdge3D(edge: Edge): Object3DType =
    val (node1, node2) = edge.nodes
    Edge3D(
      node1.position.x,
      node2.position.x,
      node1.position.y,
      node2.position.y,
      node1.position.z,
      node2.position.z,
      edge.canonicalEdgeName
    )

  private def renderLoop(): Unit =
    requestAnimationFrame((_: Double) => renderLoop())
    controls.update()
    renderer.render(scene, camera)

  override def renderScene(elementId: String): Element =
    div(
      onMountCallback { _ =>
        dom.document.getElementById(elementId).appendChild(renderer.domElement)
        renderLoop()
      }
    )

object ThreeSceneImpl:
  def apply(config: SceneConfig): ThreeScene = new ThreeSceneImpl(config)
