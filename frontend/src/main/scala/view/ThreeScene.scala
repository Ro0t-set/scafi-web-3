package view

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}
import model.Edge
import model.Node
import typings.three.examplesJsmControlsOrbitControlsMod.OrbitControls
import typings.three.mod.*
import typings.three.srcCoreObject3DMod.Object3DEventMap

import scala.scalajs.js
import component.*
class ThreeSceneImpl(width: Int, height: Int, zPointOfView: Int):

  private val scene = new Scene()
  private val camera = new PerspectiveCamera(75, width.toDouble / height, 0.1, 1000)
  camera.position.z = zPointOfView
  private val renderer = new WebGLRenderer()
  renderer.setSize(width, height)
  private val controls = new OrbitControls(camera.asInstanceOf[Camera], renderer.domElement)
  controls.enableZoom = true
  controls.enablePan = true
  controls.enableRotate = true
  controls.update()

  def setNodes(nodes: Set[Node]): Unit =
    scene.clear()
    val group = new Group()
    nodes.foreach { node =>
      val nodeObject = newNode(
        node.label,
        node.position._1,
        node.position._2,
        node.position._3
      )
      nodeObject.name = s"node-${node.label}"
      group.add(nodeObject)
    }
    scene.add(group.asInstanceOf[Object3D[Object3DEventMap]])

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