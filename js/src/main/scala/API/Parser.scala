package API
import domain.GraphDomain.GraphNode
import domain.GraphDomain.Id
import domain.GraphDomain.Position

import scala.util.Try

trait Parser[T, A]:
  def parse(t: T): Option[Set[A]]

object NodeParser extends Parser[String, GraphNode]:
  override def parse(jsonString: String): Option[Set[GraphNode]] =
    Try {
      val jsonVal = ujson.read(jsonString)
      jsonVal.arr.map { nodeJson =>
        val idValue = nodeJson("id").num.toInt
        val labelValue = nodeJson("label").str
        val colorValue = nodeJson("color").num.toInt
        val posJson    = nodeJson("position")
        val x          = posJson("x").num
        val y          = posJson("y").num
        val z          = posJson("z").num
        GraphNode(
          id = idValue,
          label = labelValue,
          color = colorValue,
          position = Position(x, y, z)
        )
      }.toSet
    }.toOption

object EdgeParser extends Parser[String, (Id, Id)]:
  override def parse(jsonString: String): Option[Set[(Id, Id)]] =
    Try {
      val jsonVal = ujson.read(jsonString)
      jsonVal.arr.map { edgeJson =>
        val source = edgeJson("source").num.toInt
        val target = edgeJson("target").num.toInt
        (source, target)
      }.toSet
    }.toOption
