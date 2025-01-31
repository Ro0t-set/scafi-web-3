import view.MainView
import view.config.ViewConfig

import scala.scalajs.js
import scala.scalajs.js.annotation._

object ViewModule:
  def apply(): MainView =
    val config = ViewConfig()
    new MainView(config)

@JSExportTopLevel("ScafiWeb3")
object ScafiWeb3:
  def main(args: Array[String]): Unit =
    ViewModule().render()
