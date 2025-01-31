package view.graph.component

import typings.three.mod._
import typings.three.srcCoreObject3DMod.Object3DEventMap
import typings.three.srcMaterialsLineBasicMaterialMod.LineBasicMaterialParameters
import view.graph.adapter.ThreeGroup
import view.graph.adapter.ThreeLine

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

private trait Edge3D extends ThreeGroup:
  @JSName("apply")
  def apply(x1: Double, x2: Double)(y1: Double, y2: Double)(
      z1: Double,
      z2: Double
  )(name: String): ThreeGroup

object Edge3dObj extends Edge3D:
  @JSName("apply")
  override def apply(x1: Double, x2: Double)(y1: Double, y2: Double)(
      z1: Double,
      z2: Double
  )(name: String): ThreeGroup =
    val group: ThreeGroup = new Group()
    val lineGeometry = new BufferGeometry().setFromPoints(js.Array(
      new Vector3(x1, y1, z1),
      new Vector3(x2, y2, z2)
    ))
    val lineMaterial = new LineBasicMaterial(new LineBasicMaterialParameters {
      color = 0xffff00
    })
    val line: ThreeLine = new Line(lineGeometry, lineMaterial)

    group.add(line)
    group.name = name
    group
