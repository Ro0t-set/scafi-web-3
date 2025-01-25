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
import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop
import org.scalacheck.Prop.forAll

import scala.scalajs.js

class AnimationStateSpec extends FunSuite with ScalaCheckSuite:

  override def beforeEach(context: BeforeEach): Unit =
    AnimationState.animationObserver.onNext(Reset())
    while AnimationState.mode.now() != ViewMode.Mode3D do
      AnimationState.animationObserver.onNext(SwitchMode())

  property("initial state validates default values") {
    Prop.all(
      Prop(AnimationState.batch.now() == 1),
      Prop(AnimationState.currentTick.now() == 0),
      Prop(AnimationState.engine.now().isEmpty),
      Prop(AnimationState.mode.now() == ViewMode.Mode3D)
    )
  }

  property("SetEngine updates engine and resets state") {
    forAll(Gen.const(js.Dynamic.literal())) { mockEngine =>
      AnimationState.animationObserver.onNext(SetEngine(mockEngine))
      Prop.all(
        Prop(AnimationState.engine.now().contains(mockEngine)),
        Prop(!AnimationState.running.now()),
        Prop(AnimationState.currentTick.now() == 0)
      )
    }
  }

  property("StartAnimation manages running state") {

    AnimationState.animationObserver.onNext(StartAnimation())
    Prop(AnimationState.running.now())

  }

  property("PauseAnimation stops animation") {

    AnimationState.animationObserver.onNext(StartAnimation())
    AnimationState.animationObserver.onNext(PauseAnimation())
    Prop(!AnimationState.running.now())

  }

  property("NextTick increments current tick") {

    val initialTick = AnimationState.currentTick.now()
    AnimationState.animationObserver.onNext(NextTick())
    Prop(AnimationState.currentTick.now() == initialTick + 1)

  }

  property("AnimationBatch updates batch value") {
    forAll(Gen.choose(1, 10)) { batchValue =>
      AnimationState.animationObserver.onNext(AnimationBatch(batchValue))
      Prop(AnimationState.batch.now() == batchValue)
    }
  }

  property("Reset restores initial state") {

    AnimationState.animationObserver.onNext(StartAnimation())
    AnimationState.animationObserver.onNext(NextTick())
    AnimationState.animationObserver.onNext(Reset())
    Prop.all(
      Prop(!AnimationState.running.now()),
      Prop(AnimationState.currentTick.now() == 0)
    )

  }

  property("SwitchMode toggles between view modes") {

    val initialMode = AnimationState.mode.now()
    AnimationState.animationObserver.onNext(SwitchMode())
    Prop(
      AnimationState.mode.now() ==
        (if initialMode == ViewMode.Mode3D then ViewMode.Mode2D
         else ViewMode.Mode3D)
    )
  }
