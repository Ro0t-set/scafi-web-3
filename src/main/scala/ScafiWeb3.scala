import it.unibo.scafi.incarnations.BasicSimulationIncarnation.AggregateProgram
import simulator.EngineImpl
import view.View


object MyProgram extends AggregateProgram:
  override def main(): Int = rep(0){_ + 1}


object ScafiWeb3:
  def main(args: Array[String]): Unit =
    EngineImpl(10, 10, 2)(100, 100, 100)(180)
    View().render()


