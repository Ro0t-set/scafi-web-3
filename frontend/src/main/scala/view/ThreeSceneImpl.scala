package view

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}
import model.Node
import typings.three.examplesJsmControlsOrbitControlsMod.OrbitControls
import typings.three.mod.*
import typings.three.srcCoreObject3DMod.Object3DEventMap


import component.*
class ThreeSceneImpl(width: Int, height: Int, zPointOfView: Int):

  private val scene = new Scene()
  private val camera = new PerspectiveCamera(75, width.toDouble / height, 0.1, 1600)
  camera.position.z = zPointOfView
  private val renderer = new WebGLRenderer()
  renderer.setSize(width, height)
  private val controls = new OrbitControls(camera.asInstanceOf[Camera], renderer.domElement)
  controls.enableZoom = true
  controls.enablePan = true
  controls.enableRotate = true
  controls.update()

  private var currentNode = Set.empty[Node]


  def setNodes(nodes: Set[Node]): Unit = {

    val nodesToRemove = currentNode.diff(nodes)
    val nodesToAdd = nodes.diff(currentNode)
    nodesToRemove.foreach { oldNode =>
      val obj = scene.getObjectByName(oldNode.id)
      if (obj != null) {
        scene.remove(obj.asInstanceOf[Object3D[Object3DEventMap]])
      }
    }

    val nodeObject = nodesToAdd.map { node => NodeFactory.createNode(node.id, node.label, node.position.x, node.position.y, node.position.z) }
    nodeObject.foreach(nodeObject =>
      scene.add(nodeObject)
    )

    currentNode = nodes
  }


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