package view.component

import typings.three.mod.{BufferGeometry, Group, Line, LineBasicMaterial, Object3D, Vector3}
import typings.three.srcCoreObject3DMod.Object3DEventMap
import typings.three.srcMaterialsLineBasicMaterialMod.LineBasicMaterialParameters

import scala.scalajs.js

def newEdge(x1 : Double, x2 : Double, y1 : Double, y2: Double):Object3D[Object3DEventMap] = {
  val group = new Group()
  val lineGeometry = new BufferGeometry().setFromPoints(js.Array(new Vector3(x1, y1, 0), new Vector3(x2, y2, 0)))
  val lineMaterial = new LineBasicMaterial(new LineBasicMaterialParameters {
    color = 0xffff00
  })
  val line = new Line(lineGeometry, lineMaterial)
  group.add(line.asInstanceOf[Object3D[Object3DEventMap]])
  group.asInstanceOf[Object3D[Object3DEventMap]]
}
