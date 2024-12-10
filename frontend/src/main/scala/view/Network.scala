package view

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom
import org.scalajs.dom.window
import typings.three.examplesJsmControlsOrbitControlsMod.OrbitControls
import typings.three.examplesJsmGeometriesTextGeometryMod.{TextGeometry, TextGeometryParameters}
import typings.three.examplesJsmLoadersFontLoaderMod
import typings.three.examplesJsmLoadersFontLoaderMod.FontLoader
import typings.three.mod.*
import typings.three.srcCoreObject3DMod.Object3DEventMap
import typings.three.srcMaterialsMeshBasicMaterialMod.MeshBasicMaterialParameters
import typings.three.srcMaterialsPointsMaterialMod.PointsMaterialParameters
import typings.three.srcMaterialsSpriteMaterialMod.SpriteMaterialParameters

import scala.language.postfixOps
import scala.scalajs.js.JSConverters.iterableOnceConvertible2JSRichIterableOnce
import scala.scalajs.js.typedarray.Float32Array

@main
def LiveGraph(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )

object Main:
  def appElement(): Element =
    div(
      h1("ScaFi Web 3"),
      renderDataGraph()
    )


  private def newNode(textLabel: String, x: Double, y: Double): Object3D[Object3DEventMap] =
    val group = new Group() // Group to hold the point and label

    // Create the point
    val pointGeometry = new SphereGeometry(5, 32, 32,
      js.undefined,
      js.undefined,
      js.undefined,
      js.undefined)

    val pointMaterial = new MeshBasicMaterial(new MeshBasicMaterialParameters {
      color = 0xff0000
    })
    val pointMesh = new Mesh(pointGeometry, pointMaterial)
    pointMesh.position.set(x, y, 0) // Set the position of the point
    group.add(pointMesh.asInstanceOf[Object3D[Object3DEventMap]])

    // Create a canvas to draw the label
    val canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
    val context = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    // Set canvas dimensions
    canvas.width = 256
    canvas.height = 64

    def drawLabel(text: String): Unit =
      context.clearRect(0, 0, canvas.width, canvas.height) // Clear previous content
      context.fillStyle = "rgba(255, 255, 255, 1.0)" // White text
      context.font = "Bold 48px Arial"
      context.textAlign = "center"
      context.textBaseline = "middle"
      context.fillText(text, canvas.width / 2, canvas.height / 2)

    drawLabel(textLabel)

    // Draw the text on the canvas
    context.fillStyle = "rgba(255, 255, 255, 1.0)" // White text
    context.font = "Bold 48px Arial"
    context.textAlign = "center"
    context.textBaseline = "middle"
    context.fillText(textLabel, canvas.width / 2, canvas.height / 2)

    // Create a texture from the canvas
    val texture = new CanvasTexture(canvas,
      js.undefined,
      js.undefined,
      js.undefined,
      js.undefined,
      js.undefined,
      js.undefined,
      js.undefined,
      js.undefined)
    texture.needsUpdate_=(true)

    // Create sprite material
    val spriteMaterial = new SpriteMaterial(new SpriteMaterialParameters {
      map = texture
      transparent = true
    })

    // Create the sprite
    val sprite = new Sprite(spriteMaterial)
    sprite.scale.set(100, 25, 1) // Adjust the scale to fit your needs
    sprite.position.set(x, y - 20, 0) // Position the label beneath the point
    group.add(sprite.asInstanceOf[Object3D[Object3DEventMap]])

    group.asInstanceOf[js.Dynamic].updateLabel = (newText: String) => {
      drawLabel(newText)
      texture.needsUpdate_=(true)// Notify Three.js that the texture has changed
    }


    group.asInstanceOf[Object3D[Object3DEventMap]]


  private def renderDataGraph(): Element =
    val scene = new Scene()
    val camera = new PerspectiveCamera(75, 1, 0.1, 1000)
    camera.position.z = 800

    val renderer = new WebGLRenderer()
    renderer.setSize(800, 800)

    val controls = new OrbitControls(camera.asInstanceOf[Camera], renderer.domElement)
    controls.enableZoom = true
    controls.enablePan = true
    controls.enableRotate = false
    controls.update()

    val nodes = for i <- 1 to 100 yield {
      val x = Math.random() * 800 - 400
      val y = Math.random() * 800 - 400
      val node = newNode(s"Node $i", x, y)
      scene.add(node)
      node
    }

    // Periodically update labels with random text
    window.setInterval(() => {
      nodes.foreach { node =>
        val updateLabel = node.asInstanceOf[js.Dynamic].updateLabel
        val randomValue = Math.floor(Math.random() * 100).toInt.toString
        updateLabel(s"Value: $randomValue")
      }
    }, 10) // Update every second

    def renderLoop(): Unit =
      window.requestAnimationFrame((_: Double) => renderLoop())
      controls.update() // Update controls
      renderer.render(scene, camera)

    // Return a Laminar div that hosts the Three.js renderer
    div(
      onMountCallback { ctx =>
        ctx.thisNode.ref.appendChild(renderer.domElement) // Attach Three.js renderer to the DOM
        renderLoop()
      }
    )

