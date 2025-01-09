package view.player

import API.GraphAPI
import domain.NextTick
import com.raquo.laminar.api.L.{*, given}
import scala.scalajs.js
import state.AnimationState.{animationObserver, batch, engine, running}
import scala.scalajs.js.JSON
import scala.scalajs.js.timers.setTimeout

trait EngineControllerImpl:
  type N = js.Dynamic
  type E = js.Dynamic
  type JsonNetwork = (N, E)

  def start(): Unit
  def processNextBatch(): JsonNetwork
  def handleNewData(net: JsonNetwork): Unit

object EngineControllerImpl extends EngineControllerImpl:
  running.signal.foreach {
    case true  => start()
    case false => ()
  }(unsafeWindowOwner)

  def processNextBatch(): JsonNetwork =
    engine.now().getOrElse(js.Dynamic.literal()).executeIterations()
    val net: N  = engine.now().getOrElse(js.Dynamic.literal()).getNodes()
    val edge: E = engine.now().getOrElse(js.Dynamic.literal()).getEdges()
    (net, edge)

  def handleNewData(net: JsonNetwork): Unit =
    net match
      case (nodes: js.Dynamic, edges: js.Dynamic) =>
        val nodesJson = JSON.stringify(nodes)
        val edgesJson = JSON.stringify(edges)
        GraphAPI.addNodesFromJson(nodesJson)
        GraphAPI.addEdgesFromJson(edgesJson)

  def start(): Unit =
    def loop(): Unit =
      if running.signal.now() then
        val batchValue = batch.signal.now()
        for _ <- 0 until batchValue - 1 do
          animationObserver.onNext(NextTick())
          engine.now().getOrElse(js.Dynamic.literal()).executeIterations()
        animationObserver.onNext(NextTick())
        val result = processNextBatch()
        handleNewData(result)
        setTimeout(0)(loop())

    loop()
