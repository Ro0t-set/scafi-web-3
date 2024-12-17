package simulator

import domain.{Id, Node, Position, SetEdgesByIds, SetNodes}
import it.unibo.scafi.config.Grid3DSettings
import state.GraphState.commandObserver
import scala.concurrent.Future

import scala.util.Random

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

object Simulator {
  def startSimulation(iterations: Int, delay: Int): Unit = {
    import it.unibo.scafi.incarnations.BasicAbstractSpatialSimulationIncarnation
    import it.unibo.scafi.space.{Point3D, SpaceHelper}

    object BasicSpatialIncarnation extends BasicAbstractSpatialSimulationIncarnation {
      override type P = Point3D
      trait MyDistanceStrategy extends DistanceStrategy
      override def buildNewSpace[E](elems: Iterable[(E, P)]): SPACE[E] =
        new Basic3DSpace(elems.toMap) with MyDistanceStrategy
    }
    import BasicSpatialIncarnation._

    object DemoSpatial extends AggregateProgram with StandardSensors {
      def main(): Int = rep(0) { _ + 1 }
    }

    val (ncols, nrows, ndeep) = (10, 10, 2)
    val (stepx, stepy, stepz) = (100, 100, 100)
    val positions: List[Point3D] = SpaceHelper.grid3DLocations(
      Grid3DSettings(nrows, ncols, ndeep, stepx, stepy, stepz, tolerance = 0)
    )
    val ids: IndexedSeq[Int] = (1 to ncols * nrows * ndeep).toIndexedSeq
    val devsToPos: Map[Int, Point3D] = ids.zip(positions).toMap

    val net = new SpaceAwareSimulator(
      space = new Basic3DSpace(devsToPos, proximityThreshold = 200),
      devs = devsToPos.map { case (d, p) => d -> new DevInfo(d, p,
        lsns = Map.empty,
        nsns => nbr => null)
      },
      simulationSeed = System.currentTimeMillis(),
      randomSensorSeed = System.currentTimeMillis()
    )

    def executeIterations(
                           current: Int,
                           iterations: Int,
                           ids: Seq[Int],
                           simulationRandom: scala.util.Random,
                           net: SpaceAwareSimulator,
                           executedNodes: Seq[Int]
                         ): Unit = {
      if (current >= iterations) {
      } else {
        val nextIdToRun = ids(simulationRandom.nextInt(ids.size))
        val updatedExecutedNodes = executedNodes :+ nextIdToRun


        Future {
          net.exec(DemoSpatial, DemoSpatial.main(), nextIdToRun)
          action(current)
        }.onComplete { _ =>
          js.Dynamic.global.requestAnimationFrame { (_: Double) =>
            executeIterations(current + 1, iterations, ids, simulationRandom, net, updatedExecutedNodes)
          }
        }
      }
    }

    def action(i: Int): Unit = {
      val nodes = net.devs.map { case (id, devInfo) =>
        Node(
          id,
          Position(devInfo.pos.x, devInfo.pos.y, devInfo.pos.z),
          net.`export`(id).map(_.root().toString).getOrElse("."),
          0x00ff
        )
      }.toSet

      val edges: Set[(Id, Id)] = net.devs
        .flatMap { case (d, _) =>
          net.neighbourhood(d).map(nbr => (d, nbr))
        }
        .filter { case (a, b) => a < b }
        .toSet

      commandObserver.onNext(SetNodes(nodes))
      commandObserver.onNext(SetEdgesByIds(edges))
    }

    executeIterations(0, iterations, ids, new Random(), net, Seq.empty)
  }
}
