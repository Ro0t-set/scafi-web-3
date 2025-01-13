package view.graph.component

import com.raquo.laminar.api.L._
import org.scalajs.dom
import typings.three.examplesJsmAddonsMod.CSS2DObject
import typings.three.mod._
import typings.three.srcCoreObject3DMod.Object3DEventMap

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@SuppressWarnings(Array("org.wartremover.warts.All"))
protected object Node2D extends ThreeNode:
  @JSName("apply")
  def apply(
      id: String,
      textLabel: String,
      x: Double,
      y: Double,
      z: Double,
      nodeColor: Int,
      name: String
  ): ThreeNode =
    val group = new Group()
    group.name = name

    val node2dId = s"node2d-$id"
    val labelId  = s"label-$id"
    Option(dom.document.getElementById(node2dId)).foreach(_.remove())
    Option(dom.document.getElementById(labelId)).foreach(_.remove())

    val pointDiv = div(
      idAttr          := node2dId,
      cls             := name,
      width           := "5px",
      height          := "5px",
      backgroundColor := "red"
    )

    val textDiv = div(
      idAttr := labelId,
      cls    := name,
      textLabel,
      color    := "white",
      fontSize := "16px"
    )

    val label = new CSS2DObject(textDiv.ref)
    val point = new CSS2DObject(pointDiv.ref)
    point.position.set(x, y, z)
    label.position.set(x + 10, y - 10, z)

    group.add(point.asInstanceOf[ThreeNode])
    group.add(label.asInstanceOf[ThreeNode])
    group.asInstanceOf[ThreeNode]
