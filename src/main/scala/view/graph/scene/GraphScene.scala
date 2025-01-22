package view.graph.scene

import com.raquo.laminar.api.L.Element
import domain.AnimationDomain.ViewMode
import domain.GraphDomain.Edge
import domain.GraphDomain.Node

trait GraphScene:
  def setNodes(newNodes: Set[Node]): Unit
  def setEdges(newEdges: Set[Edge]): Unit
  def renderScene(elementId: String): Element
  def centerView(): Unit
  def setMode(viewMode: ViewMode): Unit
