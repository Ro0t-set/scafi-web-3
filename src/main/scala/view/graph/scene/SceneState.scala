package view.graph.scene

import domain.GraphDomain.GraphEdge
import domain.GraphDomain.GraphNode
import view.graph.adapter.GenericObject3D

private[scene] case class SceneState(
    currentNodes: Set[GraphNode] = Set.empty,
    currentEdges: Set[GraphEdge] = Set.empty,
    nodeObjects: Map[String, GenericObject3D] = Map.empty,
    edgeObjects: Map[String, GenericObject3D] = Map.empty,
    viewMode: ViewModeOptions = Mode3D()
)
