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
    nodesToRemove.foreach { oldNode =>
      state.nodeObjects.get(oldNode.object3dName).foreach(
        sceneWrapper.removeObject
      )
      state = state.copy(nodeObjects = state.nodeObjects - oldNode.object3dName)
    }

  private def addNodes(nodesToAdd: Set[Node]): Unit =
    nodesToAdd.foreach { node =>
      val nodeObject = NodeFactory(node)
      state = state.copy(nodeObjects =
        state.nodeObjects + (node.object3dName -> nodeObject)
      )
      sceneWrapper.addObject(nodeObject)
    }

  private def removeEdges(edgesToRemove: Set[Edge]): Unit =
    edgesToRemove.foreach { oldEdge =>
      val edgeName = oldEdge.object3dName
      state.edgeObjects.get(edgeName).foreach(sceneWrapper.removeObject)
      state = state.copy(edgeObjects = state.edgeObjects - edgeName)
    }

  private def addEdges(edgesToAdd: Set[Edge]): Unit =
    edgesToAdd.foreach { edge =>
      val edgeName = edge.object3dName
      if !state.edgeObjects.contains(edgeName) then
        val edge3D = EdgeFactory(edge)
        state =
          state.copy(edgeObjects = state.edgeObjects + (edgeName -> edge3D))
        sceneWrapper.addObject(edge3D)
    }

  override def centerView(): Unit =
    if state.currentNodes.nonEmpty then
      state.viewMode.calculateCameraPosition(state.currentNodes).foreach {
        newPosition =>
          VectorUtils.setPosition(
            camera,
            newPosition.x,
            newPosition.y,
            newPosition.z
          )
          VectorUtils.setTarget(controls, newPosition.x, newPosition.y, 0)
          controls.update()
      }

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
