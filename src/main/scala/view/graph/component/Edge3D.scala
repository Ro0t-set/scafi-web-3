package view.graph.component

import typings.three.mod.*
import typings.three.srcCoreObject3DMod.Object3DEventMap
import typings.three.srcMaterialsLineBasicMaterialMod.LineBasicMaterialParameters
import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

@SuppressWarnings(Array("org.wartremover.warts.All"))
object Edge3D extends Object3D[Object3DEventMap]:
  @JSName("apply")
  def apply(
      x1: Double,
      x2: Double,
      y1: Double,
      y2: Double,
      z1: Double,
      z2: Double,
      name: String
  ): Object3D[Object3DEventMap] =
    val group = new Group()
    val lineGeometry = new BufferGeometry().setFromPoints(js.Array(
      new Vector3(x1, y1, z1),
      new Vector3(x2, y2, z2)
    ))
    val lineMaterial = new LineBasicMaterial(new LineBasicMaterialParameters {
      color = 0xffff00
    })
    val line = new Line(lineGeometry, lineMaterial)
    line.name = "edge-" + name
    group.add(line.asInstanceOf[Object3D[Object3DEventMap]])
    group.asInstanceOf[Object3D[Object3DEventMap]]
