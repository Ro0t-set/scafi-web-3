package view.graph.component

import typings.three.srcCoreObject3DMod.Object3DEventMap
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

    val node2dId = s"node2d-$id"
    val labelId  = s"label-$id"
    Option(dom.document.getElementById(node2dId)).foreach(_.remove())
    Option(dom.document.getElementById(labelId)).foreach(_.remove())

    val pointDiv = div(
      idAttr          := node2dId,
      cls             := "node-ojb",
      width           := "5px",
      height          := "5px",
      backgroundColor := s"red"
    )

    val textDiv = div(
      idAttr := labelId,
      cls    := "node-ojb",
      textLabel,
      color    := "white",
      fontSize := "16px"
    )

    val label = new CSS2DObject(textDiv.ref)
    val point = new CSS2DObject(pointDiv.ref)
    point.position.set(x, y, z)
    label.position.set(x + 10, y - 10, z)

    group.add(point.asInstanceOf[Object3D[Object3DEventMap]])
    group.add(label.asInstanceOf[Object3D[Object3DEventMap]])
    group.asInstanceOf[Object3D[Object3DEventMap]]
