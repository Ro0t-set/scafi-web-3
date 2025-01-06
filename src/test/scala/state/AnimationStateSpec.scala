package state

import munit.FunSuite
import com.raquo.laminar.api.L._
import domain._
import scala.scalajs.js

class AnimationStateSpec extends FunSuite:

  override def beforeEach(context: BeforeEach): Unit = {
    AnimationState.running.set(false)
    AnimationState.batch.set(1)
    AnimationState.currentTick.set(0)
    AnimationState.engine.set(js.Dynamic.literal())
  }

  test("setEngine sets the engine and resets running & currentTick") {
    // Initialize running + currentTick with non-default values
    AnimationState.running.set(true)
    AnimationState.currentTick.set(5)

    val dummyEngine = js.Dynamic.literal("name" -> "TestEngine")
    val cmd         = setEngine(dummyEngine)
    AnimationState.animationObserver.onNext(cmd)

    assertEquals(
      AnimationState.engine.now(),
      dummyEngine,
      "AnimationState.engine should be set to dummyEngine"
    )
    assertEquals(
      AnimationState.running.now(),
      false,
      "AnimationState.running should be reset to false"
    )
    assertEquals(
      AnimationState.currentTick.now(),
      0,
      "AnimationState.currentTick should be reset to 0"
    )
  }

  test("StartAnimation sets running to true only if it was false") {
    // Initially running is false
    AnimationState.animationObserver.onNext(StartAnimation())
    assertEquals(
      AnimationState.running.now(),
      true,
      "AnimationState.running should become true after StartAnimation"
    )

    // Trying to start again should keep running as true
    AnimationState.animationObserver.onNext(StartAnimation())
    assertEquals(
      AnimationState.running.now(),
      true,
      "AnimationState.running remains true if StartAnimation is called again"
    )
  }

  test("PauseAnimation sets running to false") {
    AnimationState.running.set(true)
    AnimationState.animationObserver.onNext(PauseAnimation())
    assertEquals(
      AnimationState.running.now(),
      false,
      "AnimationState.running should be set to false after PauseAnimation"
    )
  }

  test("NextTick increments currentTick") {
    AnimationState.currentTick.set(5)
    AnimationState.animationObserver.onNext(NextTick())
    assertEquals(
      AnimationState.currentTick.now(),
      6,
      "AnimationState.currentTick should be incremented by 1"
    )
  }

  test("AnimationBatch sets the batch size") {
    // Start with a known batch size of 5
    AnimationState.batch.set(5)

    AnimationState.animationObserver.onNext(AnimationBatch(10))
    assertEquals(
      AnimationState.batch.now(),
      10,
      "AnimationState.batch should be updated to 10"
    )
  }

  test("Reset resets running and currentTick, but not batch or engine") {
    AnimationState.running.set(true)
    AnimationState.currentTick.set(5)
    AnimationState.batch.set(42)

    val dummyEngine = js.Dynamic.literal("someField" -> "someValue")
    AnimationState.engine.set(dummyEngine)

    // Send Reset command
    AnimationState.animationObserver.onNext(Reset())

    assertEquals(
      AnimationState.running.now(),
      false,
      "AnimationState.running should be reset to false"
    )
    assertEquals(
      AnimationState.currentTick.now(),
      0,
      "AnimationState.currentTick should be reset to 0"
    )
    assertEquals(
      AnimationState.batch.now(),
      42,
      "AnimationState.batch should remain unchanged"
    )
    assertEquals(
      AnimationState.engine.now(),
      dummyEngine,
      "AnimationState.engine should remain unchanged"
    )
  }
