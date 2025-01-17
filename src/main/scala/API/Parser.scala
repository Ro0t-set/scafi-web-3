package API

import domain.Id
import domain.Node
import domain.Position

import scala.util.Try

trait Parser[T, A]:
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

case object EdgeParser extends Parser[String, (Id, Id)]:
  override def parse(jsonString: String): Option[Set[(Id, Id)]] =
    Try {
      val jsonVal = ujson.read(jsonString)
      jsonVal.arr.map { edgeJson =>
        val source = edgeJson("source").num.toInt
        val target = edgeJson("target").num.toInt
        (source, target)
      }.toSet
    }.toOption
