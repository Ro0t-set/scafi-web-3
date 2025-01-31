package API

import domain.GraphDomain.GraphNode
import domain.GraphDomain.Id
import domain.GraphDomain.SetEdgesByIds
import domain.GraphDomain.SetNodes
import state.GraphState._

import scala.scalajs.js.annotation._

sealed trait GraphApiError:
  def message: String

case class ParsingError(message: String) extends GraphApiError

trait GraphAPIService:
  def addNodesFromJson(input: String): Option[GraphApiError]
  def addEdgesFromJson(input: String): Option[GraphApiError]

object GraphAPI extends GraphAPIService:
  @JSExportTopLevel("addNodesFromJson")
  override def addNodesFromJson(input: String): Option[GraphApiError] =
    val maybeNodes: Option[Set[GraphNode]] = NodeParser.parse(input)
    maybeNodes match
      case Some(nodeSet) =>
        commandObserver.onNext(SetNodes(nodeSet))
        None
      case _ =>
        Some(ParsingError("Failed to parse Nodes from JSON"))

  @JSExportTopLevel("addEdgesFromJson")
  override def addEdgesFromJson(input: String): Option[GraphApiError] =
    val maybeEdges: Option[Set[(Id, Id)]] = EdgeParser.parse(input)
    maybeEdges match
      case Some(edgeSet) =>
        commandObserver.onNext(SetEdgesByIds(edgeSet))
        None
      case _ =>
        Some(ParsingError("Failed to parse Edges from JSON"))
