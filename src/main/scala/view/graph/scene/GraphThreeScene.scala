package view.graph.scene

import com.raquo.laminar.api.L.Element
import domain.{Edge, Node}

trait GraphThreeScene:
  def setNodes(newNodes: Set[Node]): Unit
  def setEdges(newEdges: Set[Edge]): Unit
  def renderScene(elementId: String): Element
  def centerView(): Unit
  def set2DMode(): Unit
  def set3DMode(): Unit
  def clearView(): Unit
