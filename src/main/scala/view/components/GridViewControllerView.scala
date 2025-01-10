package view.components

import com.raquo.laminar.api.L.*

class GridViewControllerView extends ViewComponent:
  override def render: Element =
    div (
      button("Center View",
        onClick --> { _ => println("Centering view") }
      )
    )
