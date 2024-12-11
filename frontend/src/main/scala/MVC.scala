import controller.ControllerModule
import model.ModelModule
import view.ViewModule

object MVC extends ModelModule.Interface with ViewModule.Interface with ControllerModule.Interface:
  

  override val model = new ModelImpl()
  override val view = new ViewImpl()
  override val controller = new ControllerImpl()


  @main def main(): Unit = view.renderTitle()
