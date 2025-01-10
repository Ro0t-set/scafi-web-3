package view.components

import com.raquo.laminar.api.L.*
import view.controller.EngineController

final class EngineSettingsView(controller: EngineController) extends ViewComponent:
  override def render: Element =
    div(
      cls := "engine-form-layout",
      h3("Engine Settings"),
      div(
        renderNumberInput("x", controller.xVar),
        renderNumberInput("y", controller.yVar),
        renderNumberInput("z", controller.zVar),
        renderNumberInput("distX", controller.distXVar),
        renderNumberInput("distY", controller.distYVar),
        renderNumberInput("distZ", controller.distZVar),
        renderNumberInput("edgeDist", controller.edgeDistVar)
      ),
      button(
        "Load Parameters",
        onClick --> { _ => controller.loadEngine() }
      )
    )

  private def renderNumberInput(
      inputLabel: String,
      variable: Var[Int]
  ): Element =
    div(
      label(s"$inputLabel: "),
      input(
        typ   := "number",
        value := variable.now().toString,
        onInput.mapToValue.map(_.toInt) --> variable
      )
    )
