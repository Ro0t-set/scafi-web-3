import simulator.EngineImpl
import view.View

object ScafiWeb3:
  def main(args: Array[String]): Unit =
    EngineImpl(10, 10, 2)(100, 100, 100)(180)
    View().render()
