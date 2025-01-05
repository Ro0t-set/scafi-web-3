package view.player

import API.GraphAPI
import domain.NextTick
import com.raquo.laminar.api.L.{*, given}

import scala.scalajs.js
import scala.concurrent.Promise
import state.AnimationState.{animationObserver, batch, running}

import scala.scalajs.js.JSON
case class EngineState(
    isPlaying: Boolean = false,
    batchSize: Int = 1
)

class EngineController(engine: js.Dynamic):
  private val state = Promise[EngineState]()
  state.success(EngineState())

  running.signal.foreach {
    case true  => start(); println("Start")
    case false => ()
  }(unsafeWindowOwner)

  private def processNextBatch(): js.Dynamic =
    engine.nextAndGetJsonNetwork()

  private def handleNewData(nodes: js.Dynamic): Unit =
    val nodesJson = JSON.stringify(nodes)
    GraphAPI.addNodesFromJson(nodesJson)

  def start(): Unit =
    def loop(): Unit =
      if running.signal.now() then
        val batchValue = batch.signal.now()
        for _ <- 0 until batchValue - 1 do
          animationObserver.onNext(NextTick())
          processNextBatch()

        val result = processNextBatch()
        handleNewData(result)
        js.timers.setTimeout(0)(loop())

    loop()
