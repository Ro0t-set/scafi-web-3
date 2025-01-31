package view.graph.scene

import com.raquo.laminar.api.L.Element
import domain.AnimationDomain.ViewMode
import domain.GraphDomain.GraphEdge
import domain.GraphDomain.GraphNode

trait GraphScene:
  def setNodes(newNodes: Set[GraphNode]): Unit
  def setEdges(newEdges: Set[GraphEdge]): Unit
  def renderScene(elementId: String): Element
  def centerView(): Unit
  def setMode(viewMode: ViewMode): Unit
