package view

import com.raquo.laminar.api.L.{*, given}

import scala.util.Random

final class NodeID

case class NodeItem(id: NodeID, label: String, x: Int, y: Int):
  def position: Tuple = (x, y)

object NodeItem:
  def apply(): NodeItem =
    NodeItem(NodeID(), "?", Random.nextInt(100) + 1, Random.nextInt(100) + 1)
end NodeItem

type DataList = List[NodeItem]

final class Model:
  private val dataVar: Var[DataList] = Var(List(NodeItem(NodeID(), "one", 1, 1)))
  val dataSignal: StrictSignal[DataList] = dataVar.signal

  def addNodeItem(item: NodeItem): Unit =
    dataVar.update(data => data :+ item)

  def removeNodeItem(id: NodeID): Unit =
    dataVar.update(data => data.filter(_.id != id))

  def makeNodeItemUpdater[A](id: NodeID,
                             f: (NodeItem, A) => NodeItem): Observer[A] =
    dataVar.updater { (data, newValue) =>
      data.map { item =>
        if item.id == id then f(item, newValue) else item
      }
    }
  end makeNodeItemUpdater
end Model
