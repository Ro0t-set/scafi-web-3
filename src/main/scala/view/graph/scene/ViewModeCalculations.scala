package view.graph.scene

import domain.Node
import typings.three.mod.Vector3

trait ViewModeCalculations:
  def calculateCameraPosition(nodes: Set[Node]): Option[Vector3]
  def maxDepth(nodes: Set[Node]): Double

object ViewModeCalculations extends ViewModeCalculations:

  private def bottomLeftPosition(nodes: Set[Node]): (Double, Double) =
    nodes.foldLeft((Double.MaxValue, Double.MaxValue)) {
      case ((minX, minY), node) =>
        val newX = if (node.position.x < minX) node.position.x else minX
        val newY = if (node.position.y < minY) node.position.y else minY
        (newX, newY)
    }

  private def topRightNodePosition(nodes: Set[Node]): (Double, Double) =
    nodes.foldLeft((Double.MinValue, Double.MinValue)) {
      case ((maxX, maxY), node) =>
        val newX = if (node.position.x > maxX) node.position.x else maxX
        val newY = if (node.position.y > maxY) node.position.y else maxY
        (newX, newY)
    }

  private def maxDepthNodePosition(nodes: Set[Node]): Double =
    nodes.foldLeft(Double.MinValue) {
      case (maxDepth, node) =>
        if (node.position.z > maxDepth) node.position.z else maxDepth
    }

  override def maxDepth(nodes: Set[Node]): Double = maxDepthNodePosition(nodes)

  override def calculateCameraPosition(nodes: Set[Node]): Option[Vector3] =
    val (minX, minY) = bottomLeftPosition(nodes)
    val (maxX, maxY) = topRightNodePosition(nodes)
    if minX == Double.MaxValue || minY == Double.MaxValue || maxX == Double.MinValue || maxY == Double.MinValue
    then None
    else
      Some(
        Vector3(
          (minX + maxX) / 2,
          (minY + maxY) / 2,
          Math.max(maxX, maxY)
        )
      )
