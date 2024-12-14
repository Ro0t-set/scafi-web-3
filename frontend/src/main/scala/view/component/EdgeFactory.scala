package view.component

import typings.three.mod.{BufferGeometry, Group, Line, LineBasicMaterial, Object3D, Vector3}
import typings.three.srcCoreObject3DMod.Object3DEventMap
import typings.three.srcMaterialsLineBasicMaterialMod.LineBasicMaterialParameters

import scala.scalajs.js

object EdgeFactory:
  def createEdge(x1 : Double, x2 : Double, y1 : Double, y2: Double, z1: Double, z2: Double, name: String):Object3D[Object3DEventMap] =
    val group = new Group()
    val lineGeometry = new BufferGeometry().setFromPoints(js.Array(new Vector3(x1, y1, z1), new Vector3(x2, y2, z2)))
    val lineMaterial = new LineBasicMaterial(new LineBasicMaterialParameters {
      color = 0xffff00
    })
    val line = new Line(lineGeometry, lineMaterial)
    line.name = "edge-" + name
    group.add(line.asInstanceOf[Object3D[Object3DEventMap]])
    group.asInstanceOf[Object3D[Object3DEventMap]]
