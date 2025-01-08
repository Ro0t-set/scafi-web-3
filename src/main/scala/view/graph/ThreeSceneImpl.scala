package view.graph

import com.raquo.laminar.api.L.{*, given}
import domain.{Edge, Node}
import org.scalajs.dom
import typings.three.mod.*
import view.adapter.SceneWrapper
import view.adapter.ThreeJsAdapter.*
import view.graph.component.{Edge3D, Node3D}

extension (edge: Edge)
  def canonicalEdgeName: String =
    val n1             = edge.nodes._1.id
    val n2             = edge.nodes._2.id
    val (minId, maxId) = if n1 < n2 then (n1, n2) else (n2, n1)
    s"edge-$minId-$maxId"

extension (node: Node)
  def object3dName: String = s"node-${node.id}"

@SuppressWarnings(Array("org.wartremover.warts.All"))
case class ThreeSceneImpl(width: Int, height: Int, zPointOfView: Int):
  private val sceneWrapper = SceneWrapper()
  private val scene        = sceneWrapper.underlying
  private val camera = CameraFactory.createPerspectiveCamera(
    fov = 75,
    aspect = width.toDouble / height.toDouble,
    near = 0.1,
    far = 1600
  )
  VectorUtils.setPosition(camera, width / 2.0, height / 2.0, zPointOfView)
  private val renderer = RendererFactory.createWebGLRenderer()
  renderer.setSize(width, height)
  private val controls = ControlsFactory.createOrbitControls(
    camera = camera,
    domElement = renderer.domElement.asInstanceOf[dom.HTMLElement]
  )
  controls.enableZoom = true
  controls.enablePan = true
  controls.enableRotate = true
  VectorUtils.setTarget(controls, width / 2.0, height / 2.0, 0)
  controls.update()
  private var currentNodes: Set[Node]                = Set.empty
  private var currentEdges: Set[Edge]                = Set.empty
  private var nodeObjects: Map[String, Object3DType] = Map.empty
  private var edgeObjects: Map[String, Object3DType] = Map.empty

  def setNodes(newNodes: Set[Node]): Unit =
    val nodesToAdd    = newNodes.diff(currentNodes)
    val nodesToRemove = currentNodes.diff(newNodes)
    println(s"nodesToAdd: ${nodesToAdd.size}")
    println(s"nodesToRemove: ${nodesToRemove.size}")
    nodesToRemove.foreach { oldNode =>
      nodeObjects.get(oldNode.object3dName).foreach(sceneWrapper.removeObject)
      nodeObjects -= oldNode.object3dName
    }
    nodesToAdd.foreach { node =>
      val node3D = Node3D(
        id = node.id.toString,
        textLabel = node.label,
        x = node.position.x,
        y = node.position.y,
        z = node.position.z,
        nodeColor = node.color,
        name = node.object3dName
      )
      nodeObjects = nodeObjects + (node.object3dName -> node3D)
      sceneWrapper.addObject(node3D)
    }
    currentNodes = newNodes

  def setEdges(newEdges: Set[Edge]): Unit =
    val edgesToAdd    = newEdges.diff(currentEdges)
    val edgesToRemove = currentEdges.diff(newEdges)
    println(s"edgesToAdd: ${edgesToAdd.size}")
    println(s"edgesToRemove: ${edgesToRemove.size}")
    edgesToRemove.foreach { oldEdge =>
      val edgeName = oldEdge.canonicalEdgeName
      edgeObjects.get(edgeName).foreach(sceneWrapper.removeObject)
      edgeObjects -= edgeName
    }
    edgesToAdd.foreach { edge =>
      val edgeName = edge.canonicalEdgeName
      if !edgeObjects.contains(edgeName) then
        val edge3D = Edge3D(
          edge.nodes._1.position.x,
          edge.nodes._2.position.x,
          edge.nodes._1.position.y,
          edge.nodes._2.position.y,
          edge.nodes._1.position.z,
          edge.nodes._2.position.z,
          edgeName
        )
        edgeObjects = edgeObjects + (edgeName -> edge3D)
        sceneWrapper.addObject(edge3D)
    }
    currentEdges = newEdges

  private def renderLoop(): Unit =
    dom.window.requestAnimationFrame((_: Double) => renderLoop())
    controls.update()
    renderer.render(scene, camera)

  def renderScene(elementId: String): Element =
    div(
      onMountCallback { _ =>
        dom.document.getElementById(elementId).appendChild(renderer.domElement)
        renderLoop()
      }
    )
