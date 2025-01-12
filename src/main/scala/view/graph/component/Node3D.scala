package view.graph.component

import typings.three.srcCoreObject3DMod.Object3DEventMap
import typings.three.srcMaterialsSpriteMaterialMod.SpriteMaterialParameters
import typings.three.mod.*
import org.scalajs.dom
import typings.three.srcMaterialsPointsMaterialMod.PointsMaterialParameters
import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@SuppressWarnings(Array("org.wartremover.warts.All"))
protected object Node3D extends ThreeNode:
  @JSName("apply")
  override def apply(
      id: String,
      textLabel: String,
      x: Double,
      y: Double,
      z: Double,
      nodeColor: Int,
      name: String
  ): ThreeNode =
    val group = new Group()

    val pointGeometry = new BufferGeometry()
    pointGeometry.setAttribute(
      "position",
      new Float32BufferAttribute(js.Array(x, y, z), 3)
    )
    val pointMaterial = new PointsMaterial(
      new PointsMaterialParameters {
        size = 15
        color = nodeColor
      }
    )
    val point = new Points(pointGeometry, pointMaterial)

    group.add(point.asInstanceOf[Object3D[Object3DEventMap]])

    val canvas =
      dom.document.createElement("canvas").asInstanceOf[dom.html.Canvas]
    val context =
      canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    canvas.width = 256
    canvas.height = 64

    def drawLabel(text: String): Unit =
      context.clearRect(0, 0, canvas.width, canvas.height)
      context.fillStyle = "rgba(255, 255, 255, 1.0)"
      context.font = "Bold 52px Arial"
      context.textAlign = "center"
      context.textBaseline = "middle"
      context.fillText(text, canvas.width / 2, canvas.height / 2)

    drawLabel(textLabel)

    val texture = new CanvasTexture(
      canvas,
      js.undefined,
      js.undefined,
      js.undefined,
      js.undefined,
      js.undefined,
      js.undefined,
      js.undefined,
      js.undefined
    )

    val spriteMaterial = new SpriteMaterial(new SpriteMaterialParameters {
      map = texture
      transparent = true
    })
    val sprite = new Sprite(spriteMaterial)
    sprite.scale.set(100, 25, 1)
    sprite.position.set(x, y - 20, z)

    group.name = name
    group.add(sprite.asInstanceOf[ThreeNode])

    group.asInstanceOf[ThreeNode]
