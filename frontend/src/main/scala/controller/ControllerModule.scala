package controller
import model.ModelModule
import view.ViewModule



object ControllerModule:
  trait Controller:
    def notifyChange(): Unit
  trait Provider:
    val controller: Controller
  type Requirements = ViewModule.Provider with ModelModule.Provider
  trait Component:
    context: Requirements =>
    class ControllerImpl extends Controller:
      def notifyChange(): Unit = context.view.renderDataGraph()
  trait Interface extends Provider with Component:
    self: Requirements =>