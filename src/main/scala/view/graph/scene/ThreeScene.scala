package view.graph.scene

import com.raquo.laminar.api.L._
import domain.Edge
import domain.Node
import domain.ViewMode
import org.scalajs.dom
import org.scalajs.dom.window
import typings.std.global.requestAnimationFrame
import typings.three.examplesJsmControlsOrbitControlsMod.OrbitControls
import typings.three.mod._
import view.graph.adapter.SceneWrapper
import view.graph.adapter.ThreeCamera
import view.graph.adapter.ThreeJsAdapter.CameraFactory
import view.graph.adapter.ThreeJsAdapter.ControlsFactory
import view.graph.adapter.ThreeJsAdapter.RendererFactory
import view.graph.adapter.ThreeJsAdapter.VectorUtils
import view.graph.component.ComponentFactory.EdgeFactory
import view.graph.component.ComponentFactory.NodeFactory
import view.graph.config.SceneConfig
import view.graph.extensions.DomainExtensions._

final class ThreeScene(config: SceneConfig) extends GraphScene:

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  private var state = SceneState()

  private val sceneWrapper        = SceneWrapper()
  private val scene               = sceneWrapper.underlying
  private val camera: ThreeCamera = initCamera(config)

  private val renderer = initRenderer(config)
  private val controls = initControls

  private def initCamera(config: SceneConfig): ThreeCamera =
    val cam = CameraFactory.createPerspectiveCamera(
      fov = config.fov,
      aspect = config.width.toDouble / config.height.toDouble,
      near = config.near,
      far = config.far
    )
    cam

  private def initRenderer(config: SceneConfig): WebGLRenderer =
    val renderer = RendererFactory.createWebGLRenderer()
    renderer.setPixelRatio(window.devicePixelRatio)
    renderer.setSize(config.width, config.height)
    renderer.info.autoReset = false
    renderer

  private def initControls: OrbitControls =
    val controls = ControlsFactory.createOrbitControls(
      camera = camera,
      domElement = renderer.domElement
    )
    controls

  override def setMode(viewMode: ViewMode): Unit =
    viewMode match
      case ViewMode.Mode2D => state = state.copy(viewMode = Mode2D())
      case ViewMode.Mode3D => state = state.copy(viewMode = Mode3D())

    state.viewMode.configureControls(controls)
    centerView()

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
    val removedObjects =
      for
        oldNode <- nodesToRemove.toSeq
        obj3d   <- state.nodeObjects.get(oldNode.object3dName)
      yield
        sceneWrapper.removeObject(obj3d)
        oldNode.object3dName

    state = state.copy(nodeObjects = state.nodeObjects -- removedObjects)

  private def addNodes(nodesToAdd: Set[Node]): Unit =
    val newObjects =
      for
        node <- nodesToAdd.toSeq
        nodeObject = NodeFactory(node)
      yield
        sceneWrapper.addObject(nodeObject)
        node.object3dName -> nodeObject

    state = state.copy(nodeObjects = state.nodeObjects ++ newObjects)

  private def removeEdges(edgesToRemove: Set[Edge]): Unit =
    val removedEdges =
      for
        oldEdge <- edgesToRemove.toSeq
        edgeName = oldEdge.object3dName
        edge3d <- state.edgeObjects.get(edgeName)
      yield
        sceneWrapper.removeObject(edge3d)
        edgeName

    state = state.copy(edgeObjects = state.edgeObjects -- removedEdges)

  private def addEdges(edgesToAdd: Set[Edge]): Unit =
    val newEdges =
      for
        edge <- edgesToAdd.toSeq
        edgeName = edge.object3dName
        if !state.edgeObjects.contains(edgeName)
        edge3D = EdgeFactory(edge)
      yield
        sceneWrapper.addObject(edge3D)
        edgeName -> edge3D

    state = state.copy(edgeObjects = state.edgeObjects ++ newEdges)

  override def centerView(): Unit =
    if state.currentNodes.nonEmpty then
      state.viewMode.calculateCameraPosition(state.currentNodes) match
        case Some(newPosition) =>
          VectorUtils.setPosition(
            camera,
            newPosition.x,
            newPosition.y,
            newPosition.z
          )
          VectorUtils.setTarget(controls, newPosition.x, newPosition.y, 0)
          controls.update()
        case None => ()

  private def renderLoop(): Unit =
    requestAnimationFrame(_ => renderLoop())
    renderer.render(scene, camera)

  override def renderScene(elementId: String): Element =
    div(
      onMountCallback { _ =>
        dom.document.getElementById(elementId).appendChild(renderer.domElement)
        renderLoop()
      }
    )
