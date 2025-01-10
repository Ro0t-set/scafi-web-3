package view.controller

import com.raquo.laminar.api.L.*
import domain.{PauseAnimation, SetEngine}
import state.AnimationState.animationObserver
import scala.scalajs.js

class EngineController(
    val xVar: Var[Int] = Var(10),
    val yVar: Var[Int] = Var(10),
    val zVar: Var[Int] = Var(2)
)(
    val distXVar: Var[Int] = Var(100),
    val distYVar: Var[Int] = Var(100),
    val distZVar: Var[Int] = Var(100)
)(val edgeDistVar: Var[Int] = Var(190)):

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

    animationObserver.onNext(SetEngine(newEngine))
