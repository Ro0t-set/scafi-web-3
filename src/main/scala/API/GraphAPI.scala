package API

import com.raquo.laminar.api.L.{*, given}
import domain.{Node, Position, SetNodes}
import state.GraphState.*
import scala.scalajs.js.annotation.*

object GraphAPI:
  @JSExportTopLevel("addNodesFromJson")
  def addNodesFromJson(input: String): Unit =
    val json: ujson.Value = ujson.read(input)
    val nodeSet = json.arr.map { nodeJson =>
      val idValue    = nodeJson("id").num.toInt
      val labelValue = nodeJson("label").str
      val colorValue = nodeJson("color").num.toInt
      val posJson    = nodeJson("position")
      val x          = posJson("x").num
      val y          = posJson("y").num
      val z          = posJson("z").num
      Node(
        id = idValue,
        label = labelValue,
        color = colorValue,
        position = Position(x, y, z)
      )
    }.toSet

    commandObserver.onNext(SetNodes(nodeSet))
