package view.components

import com.raquo.laminar.api.L.*
import domain.{SwitchMode, ViewMode}
import state.AnimationState
import view.graph.scene.GraphThreeScene

class GridViewControllerView(scene: GraphThreeScene) extends ViewComponent:
  private val switchLabel: Signal[String] = AnimationState.mode.map {
    case ViewMode.Mode2D =>
      scene.setMode(ViewMode.Mode2D)
      "Switch to 3D"
    case ViewMode.Mode3D =>
      scene.setMode(ViewMode.Mode3D)
      "Switch to 2D"
  }
  override def render: Element =
    div(
      cls := "grid-view-controller",
      button("Center View", onClick --> (_ => scene.centerView())),
      button(
        child.text <-- switchLabel,
        onClick --> (_ =>
          AnimationState.animationObserver.onNext(SwitchMode())
        )
      )
    )
