package API

import typings.std.JSON
import com.raquo.laminar.api.L.{*, given}
import domain.{
  AddNode,
  Color,
  GraphCommand,
  Id,
  Label,
  Node,
  Position,
  SetNodes
}
import state.GraphState.*
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation._

@JSExportTopLevel("addNodesFromJson")
def addNodeFromJson(input: Any): Unit =
  try
    val parsedNodes = input match {
      case jsArray: js.Array[js.Dynamic] => jsArray
      case jsonString: String =>
        JSON.parse(jsonString).asInstanceOf[js.Array[js.Dynamic]]
      case _ =>
        throw new IllegalArgumentException(
          "Invalid input: Expected JSON string or array of objects"
        )
    }

    val nodeSet: Set[Node] = parsedNodes.map { node =>
      Node(
        id = node.id.asInstanceOf[Id],
        position = Position(
          x = node.position.x.asInstanceOf[Double],
          y = node.position.y.asInstanceOf[Double],
          z = node.position.z.asInstanceOf[Double]
        ),
        label = node.label.asInstanceOf[Label],
        color = node.color.asInstanceOf[Color]
      )
    }.toSet

    commandObserver.onNext(SetNodes(nodeSet))
  catch
    case e: Exception =>
      println(s"Error processing JSON: ${e.getMessage}")
