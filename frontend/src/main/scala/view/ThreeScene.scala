package view

import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}
import model.Edge
import model.Node
import typings.three.examplesJsmControlsOrbitControlsMod.OrbitControls
import typings.three.mod.*
import typings.three.srcCoreObject3DMod.Object3DEventMap
import typings.three.srcMaterialsMeshBasicMaterialMod.MeshBasicMaterialParameters
import typings.three.srcMaterialsSpriteMaterialMod.SpriteMaterialParameters

import scala.scalajs.js

trait ThreeScene:
  def renderScene(): Element
  def setNode(nodes: Set[Node]): Unit
  def setEdge(edges: Set[Edge]): Unit


class ThreeSceneImpl(width: Int, height: Int, zPointOfView: Int) extends ThreeScene:

  private val scene = new Scene()
  private val camera = new PerspectiveCamera(75, 1, 0.1, 1000)
  camera.position.z = zPointOfView
  private val renderer = new WebGLRenderer()
  renderer.setSize(width, height)
  private val controls = new OrbitControls(camera.asInstanceOf[Camera], renderer.domElement)
  controls.enableZoom = true
  controls.enablePan = true
  controls.enableRotate = false
  controls.update()


  private def renderLoop(): Unit =
    dom.window.requestAnimationFrame((_: Double) => renderLoop())
    controls.update()
    renderer.render(scene, camera)

  def renderScene(): Element =
    div(
      onMountCallback { _ =>
        dom.document.body.appendChild(renderer.domElement)
        renderLoop()
      }
    )

  def setNode(nodes: Set[Node]): Unit =
    import view.component.*
    nodes.foreach(node => {
      val x = node.position._1
      val y = node.position._2
      val z = node.position._3
      val nodeObject = newNode(node.label, x, y)
      scene.add(nodeObject)
    })


  def setEdge(edges: Set[Edge]): Unit = ???





