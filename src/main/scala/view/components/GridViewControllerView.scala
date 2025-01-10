package view.components

import com.raquo.laminar.api.L.*
import view.graph.scene.GraphThreeScene

class GridViewControllerView(scene: GraphThreeScene) extends ViewComponent:
  override def render: Element =
    div(
      button("Center View", onClick --> (_ => scene.centerView()))
    )
