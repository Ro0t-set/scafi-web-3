package view.graph.scene

import domain.Node
import typings.three.examplesJsmControlsOrbitControlsMod.OrbitControls
import typings.three.mod.Vector3

object ViewModeCalculations:

  private def bottomLeftPosition(nodes: Set[Node]): (Double, Double) =
    nodes.foldLeft((Double.MaxValue, Double.MaxValue)) {
      case ((minX, minY), node) =>
        (Math.min(minX, node.position.x), Math.min(minY, node.position.y))
    }
  private def topRightNodePosition(nodes: Set[Node]): (Double, Double) =
    nodes.foldLeft((Double.MinValue, Double.MinValue)) {
      case ((maxX, maxY), node) =>
        (Math.max(maxX, node.position.x), Math.max(maxY, node.position.y))
    }

  private def maxDepthNodePosition(nodes: Set[Node]): Double =
    nodes.foldLeft(Double.MinValue) {
      case (maxDepth, node) => Math.max(maxDepth, node.position.z)
    }

  def calculateCameraPosition(nodes: Set[Node]): Option[Vector3] =
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

  def maxDepth(nodes: Set[Node]): Double = maxDepthNodePosition(nodes)

sealed trait ViewMode:
  def configureControls(controls: OrbitControls): Unit
  def calculateCameraPosition(nodes: Set[Node]): Option[Vector3]

final case class Mode3D() extends ViewMode:
  override def configureControls(controls: OrbitControls): Unit =
    controls.enableRotate = true
    controls.minPolarAngle = 0
    controls.maxPolarAngle = Math.PI
    controls.update()

  override def calculateCameraPosition(nodes: Set[Node]): Option[Vector3] =
    ViewModeCalculations.calculateCameraPosition(nodes).map(v =>
      Vector3(v.x, v.y, v.z + ViewModeCalculations.maxDepth(nodes))
    )

final case class Mode2D() extends ViewMode:
  override def configureControls(controls: OrbitControls): Unit =
    controls.enableRotate = false
    controls.update()

  override def calculateCameraPosition(nodes: Set[Node]): Option[Vector3] =
    ViewModeCalculations.calculateCameraPosition(nodes)
