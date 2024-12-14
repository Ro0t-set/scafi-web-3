package view.component

import typings.three.srcCoreObject3DMod.Object3DEventMap
import typings.three.srcMaterialsMeshBasicMaterialMod.MeshBasicMaterialParameters
import typings.three.srcMaterialsSpriteMaterialMod.SpriteMaterialParameters
import typings.three.mod.*
import org.scalajs.dom
import scala.scalajs.js

object NodeFactory:

  def createNode(id: String, textLabel: String, x: Double, y: Double, z: Double): Object3D[Object3DEventMap] =
    val group = new Group()
    group.name = id

    val pointGeometry = new SphereGeometry(5, 32, 32, js.undefined, js.undefined, js.undefined, js.undefined)
    val pointMaterial = new MeshBasicMaterial(new MeshBasicMaterialParameters {
      color = 0xff0000
    })
    val pointMesh = new Mesh(pointGeometry, pointMaterial)
    pointMesh.position.set(x, y, z)
    group.add(pointMesh.asInstanceOf[Object3D[Object3DEventMap]])


    val canvas = dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
    val context = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    canvas.width = 256
    canvas.height = 64

    def drawLabel(text: String): Unit = {
      context.clearRect(0, 0, canvas.width, canvas.height)
      context.fillStyle = "rgba(255, 255, 255, 1.0)"
      context.font = "Bold 48px Arial"
      context.textAlign = "center"
      context.textBaseline = "middle"
      context.fillText(text, canvas.width / 2, canvas.height / 2)
    }

    drawLabel(textLabel)

    // Texture da canvas
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


    val spriteMaterial = new SpriteMaterial(new SpriteMaterialParameters {
      map = texture
      transparent = true
    })
    val sprite = new Sprite(spriteMaterial)
    sprite.scale.set(100, 25, 1)
    sprite.position.set(x, y - 20, z)
    group.add(sprite.asInstanceOf[Object3D[Object3DEventMap]])


    group.asInstanceOf[js.Dynamic].updateLabel = (newText: String) => {
      drawLabel(newText)
      texture.needsUpdate_=(true)
    }

    group.asInstanceOf[Object3D[Object3DEventMap]]


