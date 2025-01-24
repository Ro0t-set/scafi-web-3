package view.graph.component

import org.scalajs.dom
import org.scalajs.dom.HTMLCanvasElement
import typings.three.mod._
import typings.three.srcCoreObject3DMod.Object3DEventMap
import typings.three.srcMaterialsPointsMaterialMod.PointsMaterialParameters
import typings.three.srcMaterialsSpriteMaterialMod.SpriteMaterialParameters
import view.graph.adapter.ThreeGroup
import view.graph.adapter.ThreeSprite

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

private trait Node3d extends ThreeGroup:
  @JSName("apply")
  def apply(
      id: String,
      textLabel: String,
      nodeColor: Int,
      name: String
  )(
      x: Double,
      y: Double,
      z: Double
  ): ThreeGroup

private object Node3dObj extends Node3d:
  @JSName("apply")
  override def apply(
      id: String,
      textLabel: String,
      nodeColor: Int,
      name: String
  )(
      x: Double,
      y: Double,
      z: Double
  ): ThreeGroup =
    val group: ThreeGroup = Group()

    val pointGeometry: BufferGeometry[Nothing] = BufferGeometry()
    pointGeometry.setAttribute(
      "position",
      Float32BufferAttribute(js.Array(x, y, z), 3)
    )
    val pointMaterial: PointsMaterial = PointsMaterial(
      new PointsMaterialParameters {
        size = 15
        color = nodeColor
      }
    )
    val point
        : Points[BufferGeometry[Nothing], PointsMaterial, Object3DEventMap] =
      Points(pointGeometry, pointMaterial)

    group.add(point)

    @SuppressWarnings(Array("org.wartremover.warts.All"))
    val canvas: HTMLCanvasElement =
      dom.document.createElement("canvas").asInstanceOf[HTMLCanvasElement]
    val context = canvas.getContext("2d")
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

    val texture: CanvasTexture = new CanvasTexture(
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

    val spriteMaterial: SpriteMaterial =
      SpriteMaterial(new SpriteMaterialParameters {
        map = texture
        transparent = true
      })
    val sprite: ThreeSprite = Sprite(spriteMaterial)
    sprite.scale.set(100, 25, 1)
    sprite.position.set(x, y - 20, z)
    group.name = name
    group.add(sprite)
    group
