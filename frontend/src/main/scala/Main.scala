package scala

import view.View
import simulator.Simulator

object Main:
  def main(args: Array[String]): Unit = View(Simulator.startSimulation).render()



