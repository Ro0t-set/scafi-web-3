package view.graph

import com.raquo.laminar.api.L.{*, given}
import domain.{Edge, Node}
import org.scalajs.dom
import typings.three.mod.*
import view.adapter.SceneWrapper
import view.adapter.ThreeJsAdapter.*
import view.graph.component.{Edge3D, Node3D}

extension (node: Node)
  def object3dName: String = "node-" + node.id.toString

extension (edge: Edge)
  def object3dName: String =
    "edge-" + edge.nodes._1.id.toString + "-" + edge.nodes._2.id.toString

@SuppressWarnings(Array("org.wartremover.warts.All"))
case class ThreeSceneImpl(width: Int, height: Int, zPointOfView: Int):
  private val sceneWrapper = SceneWrapper()
  private val scene        = sceneWrapper.underlying

  private val camera = CameraFactory.createPerspectiveCamera(
    fov = 75,
    aspect = width / height,
    near = 0.1,
    far = 1600
  )

  VectorUtils.setPosition(camera, width / 2, height / 2, zPointOfView)

  private val renderer = RendererFactory.createWebGLRenderer()
  renderer.setSize(width, height)

  private val controls = ControlsFactory.createOrbitControls(
    camera = camera,
    domElement = renderer.domElement.asInstanceOf[dom.HTMLElement]
  )

  controls.enableZoom = true
  controls.enablePan = true
  controls.enableRotate = true
  VectorUtils.setTarget(controls, width / 2, height / 2, 0)
  controls.update()

  private var currentNode = Set.empty[Node]
  private var currentEdge = Set.empty[Edge]

  def setNodes(nodes: Set[Node]): Unit =
    val nodesToAdd    = nodes.diff(currentNode)
    val nodesToRemove = currentNode.diff(nodes)

    println("nodesToAdd " + nodesToAdd.size)
    println("nodesToRemove " + nodesToRemove.size)

    nodesToRemove.foreach { oldNode =>
      sceneWrapper.findByName(oldNode.object3dName).foreach(
        sceneWrapper.removeObject
      )
    }

    val nodeObjects = nodesToAdd.map { node =>
      Node3D(
        node.id.toString,
        node.label,
        node.position.x,
        node.position.y,
        node.position.z,
        node.color,
        node.object3dName
      )
    }

    nodeObjects.foreach(sceneWrapper.addObject)
    currentNode = nodes

  def setEdges(edges: Set[Edge]): Unit =
    val edgesToAdd    = edges.diff(currentEdge)
    val edgesToRemove = currentEdge.diff(edges)

    println("edgesToRemove " + edgesToRemove.size)
    println("edgesToAdd " + edgesToAdd.size)

    val edgeObjects = edgesToAdd.map { edge =>
      Edge3D(
        edge.nodes._1.position.x,
        edge.nodes._2.position.x,
        edge.nodes._1.position.y,
        edge.nodes._2.position.y,
        edge.nodes._1.position.z,
        edge.nodes._2.position.z,
        edge.object3dName
      )
    }

    edgeObjects.foreach(sceneWrapper.addObject)
    currentEdge = edges

  private def renderLoop(): Unit =
    dom.window.requestAnimationFrame((_: Double) => renderLoop())
    controls.update()
    renderer.render(scene, camera)

  def renderScene(elementId: String): Element =
    div(
      onMountCallback { ctx =>
        dom.document.getElementById(elementId).appendChild(renderer.domElement)
        renderLoop()
      }
    )
