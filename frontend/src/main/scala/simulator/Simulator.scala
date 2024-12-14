package simulator

import domain.{AddNode, Edge, Id, Node, Position, SetEdgesByIds, SetNodes}
import it.unibo.scafi.config.Grid3DSettings
import state.GraphModel.{commandObserver, nodes}

import scala.Option.option2Iterable
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.timers.{setInterval, setTimeout}
import scala.util.Random


object Simulator :
  def startSimulation(iterations: Int, delay: Int): Unit =
    import it.unibo.scafi.config.GridSettings
    import it.unibo.scafi.incarnations.BasicAbstractSpatialSimulationIncarnation
    import it.unibo.scafi.space.{Point3D, SpaceHelper}

    println("Starting simulation")

    object BasicSpatialIncarnation extends BasicAbstractSpatialSimulationIncarnation:
      override type P = Point3D

      trait MyEuclideanStrategy extends EuclideanStrategy:
        this: Basic3DSpace[_] => override val proximityThreshold = 1

      override def buildNewSpace[E](elems: Iterable[(E, P)]): SPACE[E] =
        new Basic3DSpace(elems.toMap) with MyEuclideanStrategy

    import BasicSpatialIncarnation._


    object DemoSpatial extends AggregateProgram with StandardSensors:
      def mySensor(): Int = sense[Int]("sensor")
      def gradient(source: Boolean): Double = rep(Double.MaxValue) {
        distance =>
          mux(source) {
            0.0
          } {
            rep(0){ _ + 1 }
          }
      }

      def main(): Int = rep(0){ _ + 1 }

    val (ncols, nrows, ndeep) = (10, 10, 2)
    val (stepx, stepy, stepz) = (100, 100, 100)
    val positions: List[Point3D] = SpaceHelper.grid3DLocations(Grid3DSettings(nrows, ncols, ndeep,  stepx, stepy, stepz, tolerance = 0))
    val ids: IndexedSeq[Int] = for (i <- 1 to ncols * nrows * ndeep) yield i
    val devsToPos: Map[Int, Point3D] = ids.zip(positions).toMap


    val net = new SpaceAwareSimulator(
      space = new Basic3DSpace(devsToPos, proximityThreshold = 180),
      devs = devsToPos.map { case (d, p) => d -> new DevInfo(d, p,
        lsns = Map.empty,
        nsns => nbr => null)
      },
      simulationSeed = System.currentTimeMillis(),
      randomSensorSeed = System.currentTimeMillis()
    )

    net.addSensor(name = "sensor", value = 0)
    net.chgSensorValue(name = "sensor", ids = Set(1), value = 1)
    net.addSensor(name = "source", value = false)
    net.chgSensorValue(name = "source", ids = Set(3), value = true)
    net.addSensor(name = "sensor2", value = 0)
    net.chgSensorValue(name = "sensor2", ids = Set(98), value = 1)
    net.addSensor(name = "obstacle", value = false)
    net.chgSensorValue(name = "obstacle", ids = Set(44,45,46,54,55,56,64,65,66), value = true)
    net.addSensor(name = "label", value = "no")
    net.chgSensorValue(name = "label", ids = Set(1), value = "go")

    val simulationRandom = new Random(1)
    var executedNodes = Seq[ID]()
    for (i <- 0 until iterations) {
      setTimeout(delay) {
        val nextIdToRun = ids(simulationRandom.nextInt(ids.size))
        executedNodes :+= nextIdToRun
        net.exec(DemoSpatial,DemoSpatial.main(), nextIdToRun)
        action(i)
      }
    }


      def action(i: Int): Unit =
        val nodes = net.devs.map(d => Node(
          d._1,
          Position(d._2.pos.x, d._2.pos.y, d._2.pos.z),
          net.`export`(d._1).map(_.root().toString).getOrElse("."),
          0x00ff)).toSet

        val edges: Set[(Id, Id)] = net.devs
          .flatMap { d =>
            net.neighbourhood(d._1).map(nbr => (d._1, nbr))
          }
          .filter { case (a, b) => a < b }
          .toSet
        commandObserver.onNext(SetNodes(nodes))
        commandObserver.onNext(SetEdgesByIds(edges))


