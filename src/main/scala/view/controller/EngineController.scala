package view.controller

import com.raquo.laminar.api.L.*
import domain.{setEngine, PauseAnimation}
import state.AnimationState.animationObserver
import scala.scalajs.js

final class EngineController(
    xVar: Var[Int],
    yVar: Var[Int],
    zVar: Var[Int],
    distXVar: Var[Int],
    distYVar: Var[Int],
    distZVar: Var[Int],
    edgeDistVar: Var[Int]
):
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
