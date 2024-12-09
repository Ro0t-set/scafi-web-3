package livechart

class ModelTest extends munit.FunSuite:
  test("fullPrice") {
    val item = NodeItem(NodeID(), "test", 0.5, 5)
    assert(item.fullPrice == 2.5)
  }

  test("addDataItem") {
    val model = new Model

    val item = NodeItem(NodeID(), "test", 0.5, 2)
    model.addNodeItem(item)

    val afterItems = model.dataSignal.now()
    assert(afterItems.size == 2)
    assert(afterItems.last == item)
  }

  test("removeDataItem") {
    val model = new Model

    model.addNodeItem(NodeItem(NodeID(), "test", 0.5, 2))

    val beforeItems = model.dataSignal.now()
    assert(beforeItems.size == 2)

    model.removeNodeItem(beforeItems.head.id)

    val afterItems = model.dataSignal.now()
    assert(afterItems.size == 1)
    assert(afterItems == beforeItems.tail)
  }
end ModelTest
