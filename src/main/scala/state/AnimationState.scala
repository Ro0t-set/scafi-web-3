package state

import com.raquo.laminar.api.L._
import domain.AnimationBatch
import domain.AnimationCommand
import domain.NextTick
import domain.NextTickAdd
import domain.PauseAnimation
import domain.Reset
import domain.SetEngine
import domain.StartAnimation
import domain.SwitchMode
import domain.ViewMode

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("AnimationState")
object AnimationState:
  private val runningVar: Var[Boolean]           = Var[Boolean](false)
  private val batchVar: Var[Int]                 = Var[Int](1)
  private val currentTickVar: Var[Int]           = Var[Int](0)
  private val engineVar: Var[Option[js.Dynamic]] = Var[Option[js.Dynamic]](None)
  private val modeVar: Var[ViewMode] = Var[ViewMode](ViewMode.Mode3D)

  @JSExport
  val running: StrictSignal[Boolean] = runningVar.signal
  @JSExport
  val batch: StrictSignal[Int] = batchVar.signal
  @JSExport
  val currentTick: StrictSignal[Int] = currentTickVar.signal
  @JSExport
  val engine: StrictSignal[Option[js.Dynamic]] = engineVar.signal
  @JSExport
  val mode: StrictSignal[ViewMode] = modeVar.signal

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
      case NextTickAdd(tick: Int) => currentTickVar.update(_ + tick)
      case AnimationBatch(batch)  => batchVar.set(batch)
      case Reset()                => reset()
      case SwitchMode() => modeVar.update {
          case ViewMode.Mode2D => ViewMode.Mode3D
          case ViewMode.Mode3D => ViewMode.Mode2D
        }
    }
