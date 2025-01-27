package view.graph.scene

import domain.GraphDomain.GraphNode
import typings.three.mod.Vector3

trait ViewModeCalculations:
  def calculateCameraPosition(nodes: Set[GraphNode]): Option[Vector3]
  def maxDepth(nodes: Set[GraphNode]): Double

object ViewModeCalculations extends ViewModeCalculations:

  private def coordinateBounds(
      nodes: Set[GraphNode],
      extract: GraphNode => Double,
      compare: (Double, Double) => Double
  ): Option[Double] =
    nodes.iterator.map(extract).reduceOption(compare)

  private def minCoordinate(
      nodes: Set[GraphNode],
      extract: GraphNode => Double
  ): Option[Double] =
    coordinateBounds(nodes, extract, _ min _)

  private def maxCoordinate(
      nodes: Set[GraphNode],
      extract: GraphNode => Double
  ): Option[Double] =
    coordinateBounds(nodes, extract, _ max _)

  private def bottomLeftPosition(nodes: Set[GraphNode])
      : Option[(Double, Double)] =
    for
      minX <- minCoordinate(nodes, _.position.x)
      minY <- minCoordinate(nodes, _.position.y)
    yield (minX, minY)

  private def topRightPosition(nodes: Set[GraphNode])
      : Option[(Double, Double)] =
    for
      maxX <- maxCoordinate(nodes, _.position.x)
      maxY <- maxCoordinate(nodes, _.position.y)
    yield (maxX, maxY)

  private def maxDepthNodePosition(nodes: Set[GraphNode]): Option[Double] =
    maxCoordinate(nodes, _.position.z)

  override def maxDepth(nodes: Set[GraphNode]): Double =
    maxDepthNodePosition(nodes).getOrElse(Double.MinValue)

  override def calculateCameraPosition(nodes: Set[GraphNode]): Option[Vector3] =
    for
      (minX, minY) <- bottomLeftPosition(nodes)
      (maxX, maxY) <- topRightPosition(nodes)
    yield Vector3(
      (minX + maxX) / 2,
      (minY + maxY) / 2,
      Math.max(maxX, maxY)
    )
