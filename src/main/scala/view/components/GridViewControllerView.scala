package view.components

import com.raquo.laminar.api.L.*
import view.graph.ThreeScene

class GridViewControllerView(scene: ThreeScene) extends ViewComponent:
  override def render: Element =
    div(
      button("Center View", onClick --> (_ => scene.centerView()))
    )
