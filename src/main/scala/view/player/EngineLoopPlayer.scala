package view.player

import API.GraphAPI
import domain.AnimationDomain.NextTickAdd
import org.scalajs.dom.console
import state.AnimationState
import state.AnimationState.animationObserver
import state.AnimationState.batch
import state.AnimationState.engine
import state.AnimationState.running
import typings.std.global.setTimeout

import scala.scalajs.js
import scala.scalajs.js.JSON

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
      GraphAPI.addNodesFromJson(JSON.stringify(nodes))
      GraphAPI.addEdgesFromJson(JSON.stringify(edges))

  override def start(): Unit =
    def loop(): Unit =
      if running.now() then
        val batchCount = batch.now()
        for _ <- 1 to batchCount do getEngineOrEmpty.executeIterations()
        animationObserver.onNext(NextTickAdd(batchCount + 1))
        handleNewData(processNextBatch())
        setTimeout(() => loop(), 8)
    loop()
