package state

import com.raquo.laminar.api.L.*
import domain.{
  AnimationBatch,
  AnimationCommand,
  NextTick,
  PauseAnimation,
  Reset,
  SetEngine,
  StartAnimation,
  SwitchMode,
  ViewMode
}

import scala.scalajs.js

object AnimationState:
  private val runningVar: Var[Boolean]           = Var[Boolean](false)
  private val batchVar: Var[Int]                 = Var[Int](1)
  private val currentTickVar: Var[Int]           = Var[Int](0)
  private val engineVar: Var[Option[js.Dynamic]] = Var[Option[js.Dynamic]](None)
  private val modeVar: Var[ViewMode] = Var[ViewMode](ViewMode.Mode3D)

  val running: Signal[Boolean]           = runningVar.signal
  val batch: Signal[Int]                 = batchVar.signal
  val currentTick: Signal[Int]           = currentTickVar.signal
  val engine: Signal[Option[js.Dynamic]] = engineVar.signal
  val mode: Signal[ViewMode]             = modeVar.signal

  private def reset(): Unit =
    runningVar.set(false)
    currentTickVar.set(0)

  val animationObserver: Observer[AnimationCommand] =
    Observer[AnimationCommand] {
      case SetEngine(engine) =>
        engineVar.set(Some(engine))
        reset()
      case StartAnimation() => if !runningVar.now() then runningVar.set(true)
      case PauseAnimation() => runningVar.set(false)
      case NextTick()       => currentTickVar.update(_ + 1)
      case AnimationBatch(batch) => batchVar.set(batch)
      case Reset()               => reset()
      case SwitchMode() => modeVar.update {
          case ViewMode.Mode2D => ViewMode.Mode3D
          case ViewMode.Mode3D => ViewMode.Mode2D
        }
    }
