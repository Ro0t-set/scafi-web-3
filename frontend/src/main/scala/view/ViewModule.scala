package view

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.Element
import controller.ControllerModule

object ViewModule:
  trait View:
    def renderTitle(): Unit
    def renderDataGraph(): Unit
  trait Provider:
    val view: View
  private type Requirements = ControllerModule.Provider
  trait Component:
    context: Requirements =>
    class ViewImpl extends View:
      def renderTitle(): Unit = println("Rendering title")
      def renderDataGraph(): Unit = println("Rendering data graph")
  trait Interface extends Provider with Component:
    self: Requirements =>
