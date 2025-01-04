package API

import domain.{Node, Position}
import scala.util.Try

abstract class Parser[T, A]:
  def parse(t: T): Option[Set[A]]

case object NodeParser extends Parser[String, Node]:
  override def parse(jsonString: String): Option[Set[Node]] =
    Try {
      val jsonVal = ujson.read(jsonString)
      jsonVal.arr.map { nodeJson =>
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
    }.toOption
