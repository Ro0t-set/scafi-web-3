package view.controller

import com.raquo.laminar.api.L.*
import domain.{setEngine, PauseAnimation}
import state.AnimationState.animationObserver
import scala.scalajs.js

final class EngineController:
  val xVar        = Var(10)
  val yVar        = Var(10)
  val zVar        = Var(2)
  val distXVar    = Var(100)
  val distYVar    = Var(100)
  val distZVar    = Var(100)
  val edgeDistVar = Var(190)

  def loadEngine(): Unit =
    animationObserver.onNext(PauseAnimation())

    val newEngine = js.Dynamic.global.EngineImpl(
      xVar.now(),
      yVar.now(),
      zVar.now(),
      distXVar.now(),
      distYVar.now(),
      distZVar.now(),
      edgeDistVar.now()
    )

    animationObserver.onNext(setEngine(newEngine))
