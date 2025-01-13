package view.components

import com.raquo.laminar.api.L._
import domain.AnimationBatch
import domain.PauseAnimation
import domain.StartAnimation
import state.AnimationState.animationObserver
import state.AnimationState.batch
import state.AnimationState.currentTick

case class AnimationControllerView() extends ViewComponent:
  override def render: Element =
    div(
      cls := "animation-controller",
      renderInfoSection,
      br(),
      renderControls,
      renderBatchSlider
    )

  private def renderInfoSection: Element =
    div(
      cls := "info-section",
      p(
        strong("Batch: "),
        child.text <-- batch.map(_.toString),
        " | ",
        strong("Counter: "),
        child.text <-- currentTick.map(_.toString)
      )
    )

  private def renderControls: Element =
    div(
      cls := "controls",
      renderControlButton("play", "Start", StartAnimation()),
      renderControlButton("pause", "Pause", PauseAnimation())
    )

  private def renderControlButton(
      icon: String,
      text: String,
      action: domain.AnimationCommand
  ): Element =
    button(
      cls := "control-button",
      i(cls := s"fas fa-$icon"),
      s" $text",
      onClick --> (_ => animationObserver.onNext(action))
    )

  private def renderBatchSlider: Element =
    div(
      cls := "slider-container",
      label(
        i(cls := "fas fa-sliders-h"),
        " Animation Batch"
      ),
      input(
        idAttr  := "batch-slider",
        `type`  := "range",
        minAttr := "1",
        maxAttr := "512",
        value   := "1",
        onInput.mapToValue.map(_.toInt) --> (batchSize =>
          animationObserver.onNext(AnimationBatch(batchSize))
        )
      )
    )
