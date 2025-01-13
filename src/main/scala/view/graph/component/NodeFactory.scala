package view.graph.component

import domain.ViewMode
import domain.ViewMode.Mode2D
import domain.ViewMode.Mode3D
import view.graph.adapter.ThreeJsAdapter.Object3DType
import view.graph.extensions.DomainExtensions._

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
      node: domain.Node
  ): ThreeNode = nodeType match {
    case Mode3D => Node3D(
        node.id.toString,
        node.label,
        node.position.x,
        node.position.y,
        node.position.z,
        node.color,
        node.object3dName
      )
    case Mode2D => Node2D(
        node.id.toString,
        node.label,
        node.position.x,
        node.position.y,
        node.position.z,
        node.color,
        node.object3dName
      )
  }
}
