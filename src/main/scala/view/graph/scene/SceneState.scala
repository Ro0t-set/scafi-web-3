package view.graph.scene

import domain.{Edge, Node}
import view.adapter.ThreeJsAdapter.Object3DType

private[scene] case class SceneState(
    currentNodes: Set[Node] = Set.empty,
    currentEdges: Set[Edge] = Set.empty,
    nodeObjects: Map[String, Object3DType] = Map.empty,
    edgeObjects: Map[String, Object3DType] = Map.empty,
    viewMode: ViewMode = Mode3D()
)
