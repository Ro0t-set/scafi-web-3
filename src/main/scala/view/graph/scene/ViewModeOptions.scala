package view.graph.scene

import domain.Node
import typings.three.examplesJsmControlsOrbitControlsMod.OrbitControls
import typings.three.mod.Vector3
import view.graph.extensions.DomainExtensions._

sealed trait ViewModeOptions:
  def configureControls(controls: OrbitControls): Unit
  def calculateCameraPosition(nodes: Set[Node]): Option[Vector3]

final case class Mode3D() extends ViewModeOptions:
  override def configureControls(controls: OrbitControls): Unit =
    controls.enableRotate = true
    controls.minPolarAngle = 0
    controls.maxPolarAngle = Math.PI
    controls.update()

  override def calculateCameraPosition(nodes: Set[Node]): Option[Vector3] =
    ViewModeCalculations.calculateCameraPosition(nodes).map(v =>
      Vector3(v.x, v.y, v.z + ViewModeCalculations.maxDepth(nodes))
    )

final case class Mode2D() extends ViewModeOptions:
  override def configureControls(controls: OrbitControls): Unit =
    controls.enableRotate = false
    controls.update()

  override def calculateCameraPosition(nodes: Set[Node]): Option[Vector3] =
    ViewModeCalculations.calculateCameraPosition(nodes).map(v =>
      Vector3(v.x, v.y, v.z + ViewModeCalculations.maxDepth(nodes) - 20)
    )
