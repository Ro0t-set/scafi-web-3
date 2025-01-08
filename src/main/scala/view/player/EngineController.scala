package view.player

import API.GraphAPI
import domain.NextTick
import com.raquo.laminar.api.L.{*, given}

import scala.scalajs.js
import state.AnimationState.{animationObserver, batch, engine, running}

import scala.scalajs.js.JSON
import scala.scalajs.js.timers.setTimeout

object EngineController:
  private type n           = js.Dynamic
  private type e           = js.Dynamic
  private type JsonNetwork = (n, e)

  running.signal.foreach {
    case true  => start()
    case false => ()
  }(unsafeWindowOwner)
  private def processNextBatch(): JsonNetwork =
    engine.now().getOrElse(js.Dynamic.literal()).executeIterations()
    val net: n  = engine.now().getOrElse(js.Dynamic.literal()).getNodes()
    val edge: e = engine.now().getOrElse(js.Dynamic.literal()).getEdges()
    (net, edge)

  private def handleNewData(net: JsonNetwork): Unit =
    net match
      case (nodes: js.Dynamic, edges: js.Dynamic) =>
        val nodesJson: String = JSON.stringify(nodes)
        val edgesJson: String = JSON.stringify(edges)
        GraphAPI.addNodesFromJson(nodesJson)
        GraphAPI.addEdgesFromJson(edgesJson)

  private def start(): Unit =
    def loop(): Unit =
      if running.signal.now() then
        val batchValue = batch.signal.now()
        for _ <- 0 until batchValue - 1 do
          animationObserver.onNext(NextTick())
          processNextBatch()
        animationObserver.onNext(NextTick())
        val result = processNextBatch()
        handleNewData(result)
        setTimeout(0)(loop())

    loop()
