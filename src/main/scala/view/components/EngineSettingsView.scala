package view.components

import com.raquo.laminar.api.L.{*, given}
import view.controller.EngineController

final class EngineSettingsView(controller: EngineController):
  private val xVar        = Var(10)
  private val yVar        = Var(10)
  private val zVar        = Var(2)
  private val distXVar    = Var(100)
  private val distYVar    = Var(100)
  private val distZVar    = Var(100)
  private val edgeDistVar = Var(190)

  def render: Element =
    div(
      cls := "engine-form-layout",
      h3("Engine Settings"),
      div(
        renderNumberInput("x", xVar),
        renderNumberInput("y", yVar),
        renderNumberInput("z", zVar),
        renderNumberInput("distX", distXVar),
        renderNumberInput("distY", distYVar),
        renderNumberInput("distZ", distZVar),
        renderNumberInput("edgeDist", edgeDistVar)
      ),
      button(
        "Load Parameters",
        onClick --> { _ => controller.loadEngine() }
      )
    )

  private def renderNumberInput(label: String, variable: Var[Int]): Element =
    div(
      // label(s"$label: "),
      input(
        typ   := "number",
        value := variable.now().toString,
        onInput.mapToValue.map(_.toInt) --> variable
      )
    )
