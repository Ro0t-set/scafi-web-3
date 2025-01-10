package view.components

import com.raquo.laminar.api.L.*
import domain.{SwitchMode, ViewMode}
import state.AnimationState
import view.graph.scene.GraphThreeScene

class GridViewControllerView(scene: GraphThreeScene) extends ViewComponent:
  private val switchLabel: Signal[String] = AnimationState.mode.signal.map {
    case ViewMode.Mode2D =>
      scene.set2DMode()
      "Switch to 3D"
    case ViewMode.Mode3D =>
      scene.set3DMode()
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
