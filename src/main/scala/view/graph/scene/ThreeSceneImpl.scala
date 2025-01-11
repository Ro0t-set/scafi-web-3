package view.graph.scene

import com.raquo.laminar.api.L.*
import domain.{Edge, Node}
import org.scalajs.dom
import org.scalajs.dom.{document, window}
import org.scalajs.dom.window.requestAnimationFrame
import typings.three.examplesJsmAddonsMod.CSS2DRenderer
import typings.three.examplesJsmControlsOrbitControlsMod.OrbitControls
import typings.three.mod.*
import view.graph.adapter.SceneWrapper
import view.graph.adapter.ThreeJsAdapter.{
  CameraFactory,
  ControlsFactory,
  Object3DType,
  RendererFactory,
  VectorUtils
}
import view.graph.component.{Edge3D, Node2D, Node3D}
import view.graph.config.{SceneConfig, ViewMode}
import view.graph.extensions.DomainExtensions.*

@SuppressWarnings(Array("org.wartremover.warts.All"))
final class ThreeSceneImpl(config: SceneConfig) extends GraphThreeScene:
  private var state    = SceneState()
  private var viewMode = ViewMode()

  private val sceneWrapper   = SceneWrapper()
  private val scene          = sceneWrapper.underlying
  private val camera         = initCamera(config)
  private val renderer       = initRenderer(config)
  private val labelsRenderer = initCSS2dRenderer(config)
  private val controls       = initControls(config)

  private def initCamera(config: SceneConfig): PerspectiveCamera =
    CameraFactory.createPerspectiveCamera(
      fov = config.fov,
      aspect = config.width.toDouble / config.height.toDouble,
      near = config.near,
      far = config.far
    )

  private def initRenderer(config: SceneConfig): WebGLRenderer =
    val renderer = RendererFactory.createWebGLRenderer()
    renderer.setPixelRatio(window.devicePixelRatio)
    renderer.setSize(config.width, config.height)
    renderer

  private def initCSS2dRenderer(config: SceneConfig): CSS2DRenderer =
    val labelRenderer = new CSS2DRenderer()
    labelRenderer.setSize(config.width, config.height)
    labelRenderer.domElement.style.position = "absolute"
    labelRenderer.domElement.style.top = "0"
    labelRenderer.domElement.style.pointerEvents = "none"
    labelRenderer

  private def initControls(config: SceneConfig): OrbitControls =
    val controls = ControlsFactory.createOrbitControls(
      camera = camera,
      domElement = renderer.domElement
    )
    controls

  override def set2DMode(): Unit =
    state = state.copy(viewMode = Mode2D())
    state.viewMode.configureControls(controls)
    centerView()

  override def set3DMode(): Unit =
    state = state.copy(viewMode = Mode3D())
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

  override def clearView(): Unit =
    state.nodeObjects.values.foreach(sceneWrapper.removeObject)
    state.edgeObjects.values.foreach(sceneWrapper.removeObject)
    state = SceneState()

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
      val nodeObject = state.viewMode.createNodeObj(node)
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
      edge.object3dName
    )

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
    if state.viewMode == Mode2D() then
      dom.document.getElementsByClassName("node-label").foreach(_.remove())
    controls.update()
    labelsRenderer.render(scene, camera.asInstanceOf[Camera])
    renderer.render(scene, camera)

  override def renderScene(elementId: String): Element =
    div(
      onMountCallback { _ =>
        dom.document.getElementById(elementId).appendChild(
          renderer.domElement
        )
        dom.document.getElementById(
          elementId
        ) append labelsRenderer.domElement
        renderLoop()
      }
    )
