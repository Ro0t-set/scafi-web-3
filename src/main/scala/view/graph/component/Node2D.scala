package view.graph.component

import typings.three.srcCoreObject3DMod.Object3DEventMap
import typings.three.srcMaterialsPointsMaterialMod.PointsMaterialParameters
import typings.three.mod.*
import com.raquo.laminar.api.L.*
import typings.three.examplesJsmAddonsMod.CSS2DObject
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@SuppressWarnings(Array("org.wartremover.warts.All"))
object Node2D extends Object3D[Object3DEventMap]:
  @JSName("apply")
  def apply(
      id: String,
      textLabel: String,
      x: Double,
      y: Double,
      z: Double,
      nodeColor: Int,
      name: String
  ): Object3D[Object3DEventMap] =
    val group = new Group()
    group.name = name

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

    val labelId = s"label-$id"
    Option(dom.document.getElementById(labelId)).foreach(_.remove())
    val textDiv = div(
      idAttr := labelId,
      cls    := "node-label",
      textLabel,
      color    := "white",
      fontSize := "16px"
    )

    val label = new CSS2DObject(textDiv.ref)
    label.position.set(x + 10, y - 50, z)

    group.add(label.asInstanceOf[Object3D[Object3DEventMap]])
    group.asInstanceOf[Object3D[Object3DEventMap]]
