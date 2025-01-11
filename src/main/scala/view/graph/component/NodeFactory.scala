package view.graph.component

import domain.ViewMode
import domain.ViewMode.{Mode2D, Mode3D}
import view.graph.adapter.ThreeJsAdapter.Object3DType

import scala.scalajs.js.annotation.JSName

protected trait ThreeNode extends Object3DType:
  @JSName("apply")
  def apply(
      id: String,
      textLabel: String,
      x: Double,
      y: Double,
      z: Double,
      nodeColor: Int,
      name: String
  ): ThreeNode

object NodeFactory {
  def apply(nodeType: ViewMode)(
      id: Int,
      textLabel: String,
      x: Double,
      y: Double,
      z: Double,
      nodeColor: Int,
      name: String
  ): ThreeNode = nodeType match {
    case Mode2D => Node3D(id.toString, textLabel, x, y, z, nodeColor, name)
    case Mode3D => Node2D(id.toString, textLabel, x, y, z, nodeColor, name)
  }
}
