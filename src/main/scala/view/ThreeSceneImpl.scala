package view

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}
import domain.{Edge, Node}
import typings.three.examplesJsmControlsOrbitControlsMod.OrbitControls
import typings.three.mod.*
import typings.three.srcCoreObject3DMod.Object3DEventMap
import component.*
import typings.std.WebGLPowerPreference
import typings.three.srcRenderersWebGLRendererMod.WebGLRendererParameters
class ThreeSceneImpl(width: Int, height: Int, zPointOfView: Int):

  private val scene = new Scene()
  private val camera = new PerspectiveCamera(75, width.toDouble / height, 0.1, 1600)
  camera.position.z = zPointOfView
  private val renderer = new WebGLRenderer(
    new WebGLRendererParameters {
      powerPreference  =  WebGLPowerPreference.`high-performance`
      precision = "lowp"
    }
  )
  renderer.setSize(width, height)
  private val controls = new OrbitControls(camera.asInstanceOf[Camera], renderer.domElement)
  controls.enableZoom = true
  controls.enablePan = true
  controls.enableRotate = true
  controls.update()

  private var currentNode = Set.empty[Node]
  private var currentEdge = Set.empty[Edge]


  def setNodes(nodes: Set[Node]): Unit =
    val nodesToRemove = nodes.diff(currentNode)
    val nodesToAdd = nodes.diff(currentNode)

    nodesToRemove.foreach { oldNode =>
      val obj = scene.getObjectByName("node-"+oldNode.id.toString)
      if (obj != null) {
        scene.remove(obj.asInstanceOf[Object3D[Object3DEventMap]])
      }
    }
    val nodeObject = nodesToAdd.map { node => NodeFactory.createNode(node.id.toString, node.label, node.position.x, node.position.y, node.position.z) }
    nodeObject.foreach(nodeObject =>
      scene.add(nodeObject)
    )
    currentNode = nodes

  def setEdges(edges: Set[Edge]): Unit =
    val edgesToRemove = currentEdge.diff(edges)
    val edgesToAdd = edges.diff(currentEdge)

    edgesToRemove.foreach { oldEdge =>
      val obj = scene.getObjectByName("edge-"+oldEdge.nodes._1.id.toString + oldEdge.nodes._2.id.toString)
      if (obj != null) {
        scene.remove(obj.asInstanceOf[Object3D[Object3DEventMap]])
      }
    }
    val edgeObject = edgesToAdd.map { edge => EdgeFactory.createEdge(
      edge.nodes._1.position.x, edge.nodes._2.position.x,
      edge.nodes._1.position.y, edge.nodes._2.position.y,
      edge.nodes._1.position.z, edge.nodes._2.position.z,
      edge.nodes._1.id.toString + edge.nodes._2.id.toString
    ) }
    edgeObject.foreach(edgeObject =>
      scene.add(edgeObject)
    )
    currentEdge = edges



  private def renderLoop(): Unit =
    dom.window.requestAnimationFrame((_: Double) => renderLoop())
    controls.update()
    renderer.render(scene, camera)

  def renderScene(): Element =
    div(
      onMountCallback { ctx =>
        dom.document.body.appendChild(renderer.domElement)
        renderLoop()
      }
    )