package view.player

import API.GraphAPI
import domain.NextTickAdd
import org.scalajs.dom.console
import state.AnimationState

import scala.scalajs.js
import scala.scalajs.js.JSON
import state.AnimationState.{animationObserver, batch, engine, running}
import typings.std.global.setTimeout

trait EngineLoopPlayer[N, E]:
  private type JsonNetwork = (N, E)
  def start(): Unit
  def loadNextFrame(): Unit
  def processNextBatch(): JsonNetwork
  def handleNewData(net: JsonNetwork): Unit

type JsonNode = js.Dynamic
type JsonEdge = js.Dynamic

object EngineLoopPlayer extends EngineLoopPlayer[JsonNode, JsonEdge]:

  private def getEngineOrEmpty: js.Dynamic =
    engine.now().getOrElse {
      console.error("Engine not found")
      js.Dynamic.literal()
    }

  override def loadNextFrame(): Unit =
    handleNewData((getEngineOrEmpty.getNodes(), getEngineOrEmpty.getEdges()))

  override def processNextBatch(): (JsonNode, JsonEdge) =
    val currentEngine = getEngineOrEmpty
    getEngineOrEmpty.executeIterations()
    (currentEngine.getNodes(), currentEngine.getEdges())

  override def handleNewData(net: (JsonNode, JsonEdge)): Unit = net match
    case (nodes, edges) =>
      for
        nodesJson <- Option(JSON.stringify(nodes))
        edgesJson <- Option(JSON.stringify(edges))
      do
        GraphAPI.addNodesFromJson(nodesJson)
        GraphAPI.addEdgesFromJson(edgesJson)

  override def start(): Unit =
    def loop(): Unit =
      if running.now() then
        val batchCount = batch.now()
        (1 until batchCount).foreach { _ =>
          getEngineOrEmpty.executeIterations()
        }

        animationObserver.onNext(NextTickAdd(batchCount + 1))
        handleNewData(processNextBatch())
        setTimeout(() => loop(), 16)

    loop()
