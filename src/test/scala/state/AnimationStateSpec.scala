package state
import domain.AnimationDomain.AnimationBatch
import domain.AnimationDomain.NextTick
import domain.AnimationDomain.PauseAnimation
import domain.AnimationDomain.Reset
import domain.AnimationDomain.SetEngine
import domain.AnimationDomain.StartAnimation
import domain.AnimationDomain.SwitchMode
import domain.AnimationDomain.ViewMode
import munit.FunSuite

import scala.scalajs.js

class AnimationStateSpec extends FunSuite:
  test("initial state should have expected default values") {
    assertEquals(AnimationState.running.now(), false)
    assertEquals(AnimationState.batch.now(), 1)
    assertEquals(AnimationState.currentTick.now(), 0)
    assertEquals(AnimationState.engine.now(), None)
    assertEquals(
      AnimationState.mode.now(),
      ViewMode.Mode3D
    )
  }

  test("SetEngine command should update engine and reset state") {
    val mockEngine = js.Dynamic.literal()
    AnimationState.animationObserver.onNext(SetEngine(mockEngine))

    assertEquals(
      AnimationState.engine.now(),
      Some(mockEngine)
    )
    assertEquals(AnimationState.running.now(), false)
    assertEquals(AnimationState.currentTick.now(), 0)
  }

  test("StartAnimation should set running to true") {
    AnimationState.animationObserver.onNext(StartAnimation())
    assertEquals(AnimationState.running.now(), true)
  }

  test("StartAnimation should not affect running state if already running") {
    AnimationState.animationObserver.onNext(StartAnimation())
    val initialRunningState =
      AnimationState.running.now()
    AnimationState.animationObserver.onNext(StartAnimation())
    assertEquals(
      AnimationState.running.now(),
      initialRunningState
    )
  }

  test("PauseAnimation should set running to false") {
    AnimationState.animationObserver.onNext(StartAnimation())
    AnimationState.animationObserver.onNext(PauseAnimation())
    assertEquals(AnimationState.running.now(), false)
  }

  test("NextTick should increment currentTick") {
    val initialTick =
      AnimationState.currentTick.now()
    AnimationState.animationObserver.onNext(NextTick())
    assertEquals(
      AnimationState.currentTick.now(),
      initialTick + 1
    )
  }

  test("AnimationBatch should update batch value") {
    val newBatchValue = 5
    AnimationState.animationObserver.onNext(AnimationBatch(newBatchValue))
    assertEquals(
      AnimationState.batch.now(),
      newBatchValue
    )
  }

  test("Reset should set running to false and currentTick to 0") {
    AnimationState.animationObserver.onNext(StartAnimation())
    AnimationState.animationObserver.onNext(NextTick())
    AnimationState.animationObserver.onNext(Reset())

    assertEquals(AnimationState.running.now(), false)
    assertEquals(AnimationState.currentTick.now(), 0)
  }

  test("SwitchMode should toggle between 2D and 3D modes") {
    assertEquals(
      AnimationState.mode.now(),
      ViewMode.Mode3D
    )

    AnimationState.animationObserver.onNext(SwitchMode())
    assertEquals(
      AnimationState.mode.now(),
      ViewMode.Mode2D
    )

    AnimationState.animationObserver.onNext(SwitchMode())
    assertEquals(
      AnimationState.mode.now(),
      ViewMode.Mode3D
    )
  }

  override def beforeEach(context: BeforeEach): Unit =
    AnimationState.animationObserver.onNext(Reset())
    while AnimationState.mode.now() != ViewMode.Mode3D
    do
      AnimationState.animationObserver.onNext(SwitchMode())
