package state

import com.raquo.laminar.api.L.*
import domain.{AnimationCommand, *}

object AnimationState:

  val isRunning: Var[Boolean] = Var[Boolean](false)
  val batch: Var[Int] = Var[Int](1)
  val currentTick: Var[Int] = Var[Int](0)

  val animationObserver: Observer[AnimationCommand] = Observer[AnimationCommand] {
    case StartAnimation() => isRunning.set(true)
    case PauseAnimation() => isRunning.set(false)
    case nextTick() => currentTick.update(_ + 1)
    case AnimationBatch(batch) => this.batch.set(batch)
    case Reset() =>
      isRunning.set(false)
      currentTick.set(0)
      batch.set(1)
  }
