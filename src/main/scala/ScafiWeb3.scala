import it.unibo.scafi.incarnations.BasicSimulationIncarnation.AggregateProgram

import view.View


object MyProgram extends AggregateProgram:
  override def main(): Int = rep(0){_ + 1}


object ScafiWeb3:
  def main(args: Array[String]): Unit = View().render()


