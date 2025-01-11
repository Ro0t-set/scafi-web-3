package state

import munit.FunSuite
import com.raquo.laminar.api.L._
import domain._
import scala.scalajs.js

class AnimationStateSpec extends FunSuite:
  test("initial state should have expected default values") {
    assertEquals(AnimationState.running.observe(unsafeWindowOwner).now(), false)
    assertEquals(AnimationState.batch.observe(unsafeWindowOwner).now(), 1)
    assertEquals(AnimationState.currentTick.observe(unsafeWindowOwner).now(), 0)
    assertEquals(AnimationState.engine.observe(unsafeWindowOwner).now(), None)
    assertEquals(
      AnimationState.mode.observe(unsafeWindowOwner).now(),
      ViewMode.Mode3D
    )
  }

  test("SetEngine command should update engine and reset state") {
    val mockEngine = js.Dynamic.literal()
    AnimationState.animationObserver.onNext(SetEngine(mockEngine))

    assertEquals(
      AnimationState.engine.observe(unsafeWindowOwner).now(),
      Some(mockEngine)
    )
    assertEquals(AnimationState.running.observe(unsafeWindowOwner).now(), false)
    assertEquals(AnimationState.currentTick.observe(unsafeWindowOwner).now(), 0)
  }

  test("StartAnimation should set running to true") {
    AnimationState.animationObserver.onNext(StartAnimation())
    assertEquals(AnimationState.running.observe(unsafeWindowOwner).now(), true)
  }

  test("StartAnimation should not affect running state if already running") {
    AnimationState.animationObserver.onNext(StartAnimation())
    val initialRunningState =
      AnimationState.running.observe(unsafeWindowOwner).now()
    AnimationState.animationObserver.onNext(StartAnimation())
    assertEquals(
      AnimationState.running.observe(unsafeWindowOwner).now(),
      initialRunningState
    )
  }

  test("PauseAnimation should set running to false") {
    AnimationState.animationObserver.onNext(StartAnimation())
    AnimationState.animationObserver.onNext(PauseAnimation())
    assertEquals(AnimationState.running.observe(unsafeWindowOwner).now(), false)
  }

  test("NextTick should increment currentTick") {
    val initialTick =
      AnimationState.currentTick.observe(unsafeWindowOwner).now()
    AnimationState.animationObserver.onNext(NextTick())
    assertEquals(
      AnimationState.currentTick.observe(unsafeWindowOwner).now(),
      initialTick + 1
    )
  }

  test("AnimationBatch should update batch value") {
    val newBatchValue = 5
    AnimationState.animationObserver.onNext(AnimationBatch(newBatchValue))
    assertEquals(
      AnimationState.batch.observe(unsafeWindowOwner).now(),
      newBatchValue
    )
  }

  test("Reset should set running to false and currentTick to 0") {
    AnimationState.animationObserver.onNext(StartAnimation())
    AnimationState.animationObserver.onNext(NextTick())
    AnimationState.animationObserver.onNext(Reset())

    assertEquals(AnimationState.running.observe(unsafeWindowOwner).now(), false)
    assertEquals(AnimationState.currentTick.observe(unsafeWindowOwner).now(), 0)
  }

  test("SwitchMode should toggle between 2D and 3D modes") {
    assertEquals(
      AnimationState.mode.observe(unsafeWindowOwner).now(),
      ViewMode.Mode3D
    )

    AnimationState.animationObserver.onNext(SwitchMode())
    assertEquals(
      AnimationState.mode.observe(unsafeWindowOwner).now(),
      ViewMode.Mode2D
    )

    AnimationState.animationObserver.onNext(SwitchMode())
    assertEquals(
      AnimationState.mode.observe(unsafeWindowOwner).now(),
      ViewMode.Mode3D
    )
  }

  override def beforeEach(context: BeforeEach): Unit =
    AnimationState.animationObserver.onNext(Reset())
    while AnimationState.mode.observe(
        unsafeWindowOwner
      ).now() != ViewMode.Mode3D
    do
      AnimationState.animationObserver.onNext(SwitchMode())
