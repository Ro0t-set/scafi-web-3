package view.component

import typings.three.srcCoreObject3DMod.Object3DEventMap
import typings.three.srcMaterialsMeshBasicMaterialMod.MeshBasicMaterialParameters
import typings.three.srcMaterialsSpriteMaterialMod.SpriteMaterialParameters
import typings.three.mod.*
import org.scalajs.dom
import scala.scalajs.js

def newNode(textLabel: String, x: Double, y: Double, z:Double): Object3D[Object3DEventMap] =
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
  pointMesh.position.set(x, y, z) // Set the position of the point
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
  sprite.position.set(x, y - 20, z) // Position the label beneath the point
  group.add(sprite.asInstanceOf[Object3D[Object3DEventMap]])

  group.asInstanceOf[js.Dynamic].updateLabel = (newText: String) => {
    drawLabel(newText)
    texture.needsUpdate_=(true)// Notify Three.js that the texture has changed
  }


  group.asInstanceOf[Object3D[Object3DEventMap]]
