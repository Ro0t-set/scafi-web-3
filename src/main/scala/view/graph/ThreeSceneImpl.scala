package view.graph

import com.raquo.laminar.api.L.{*, given}
import domain.{Edge, Node}
import org.scalajs.dom
import typings.three.mod.*
import view.adapter.SceneWrapper
import view.adapter.ThreeJsAdapter.*
import view.graph.component.{Edge3D, Node3D}

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
    val nodesToRemove = currentNode.diff(nodes)
    val nodesToAdd    = nodes.diff(currentNode)

    nodesToRemove.foreach { oldNode =>
      sceneWrapper.findByName(s"node-${oldNode.id}")
        .foreach(sceneWrapper.removeObject)
    }

    val nodeObjects = nodesToAdd.map { node =>
      Node3D(
        node.id.toString,
        node.label,
        node.position.x,
        node.position.y,
        node.position.z,
        node.color
      )
    }

    nodeObjects.foreach(sceneWrapper.addObject)
    currentNode = nodes

  def setEdges(edges: Set[Edge]): Unit =
    val edgesToRemove = currentEdge.diff(edges)
    val edgesToAdd    = edges.diff(currentEdge)


    val edgeObjects: Unit = edgesToAdd.map { edge =>
      Edge3D(
        edge.nodes._1.position.x,
        edge.nodes._2.position.x,
        edge.nodes._1.position.y,
        edge.nodes._2.position.y,
        edge.nodes._1.position.z,
        edge.nodes._2.position.z,
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
