package view.graph.scene

import domain.Edge
import domain.Node
import view.graph.adapter.GenericObject3D

private[scene] case class SceneState(
    currentNodes: Set[Node] = Set.empty,
    currentEdges: Set[Edge] = Set.empty,
    nodeObjects: Map[String, GenericObject3D] = Map.empty,
    edgeObjects: Map[String, GenericObject3D] = Map.empty,
    viewMode: ViewModeOptions = Mode3D()
)
