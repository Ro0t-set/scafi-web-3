import simulator.EngineImpl
import view.View

import scala.scalajs.js.annotation._

@JSExportTopLevel("ScafiWeb3")
object ScafiWeb3:
  @JSExport var engineVal: EngineImpl =
    EngineImpl(10, 10, 2)(100, 100, 100)(180)
  def main(args: Array[String]): Unit =
    View().render()
