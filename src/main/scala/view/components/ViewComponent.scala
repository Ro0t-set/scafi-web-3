package view.components

import com.raquo.laminar.api.L.Element

trait ViewComponent:
  def render: Element
