package view.player

import API.GraphAPI
import domain.NextTick
import com.raquo.laminar.api.L.{*, given}

import scala.scalajs.js
import state.AnimationState.{animationObserver, batch, running}

import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport

class EngineController(engine: js.Dynamic):
  private val death: Var[Boolean] = Var(false)

  running.signal.foreach {
    case true  => start()
    case false => ()
  }(unsafeWindowOwner)

  @JSExport
  def kill(): Unit = death.set(true)

  private def processNextBatch(): js.Dynamic =
    engine.nextAndGetJsonNetwork()

  private def handleNewData(nodes: js.Dynamic): Unit =
    val nodesJson = JSON.stringify(nodes)
    GraphAPI.addNodesFromJson(nodesJson)

  def start(): Unit =
    def loop(): Unit =
      if running.signal.now() && !death.now() then
        val batchValue = batch.signal.now()
        for _ <- 0 until batchValue - 1 do
          animationObserver.onNext(NextTick())
          processNextBatch()

        animationObserver.onNext(NextTick())
        val result = processNextBatch()
        handleNewData(result)
        js.timers.setTimeout(0)(loop())

    loop()
