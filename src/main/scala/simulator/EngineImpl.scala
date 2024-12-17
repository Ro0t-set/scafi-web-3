package simulator

import it.unibo.scafi.config.Grid3DSettings
import it.unibo.scafi.incarnations.BasicAbstractSpatialSimulationIncarnation
import domain.{AnimationBatch, Edge, Id, NextTick, Node, PauseAnimation, Position, Reset, SetEdgesByIds, SetNodes, StartAnimation}
import state.AnimationState.{animationObserver, batch, running}
import state.GraphState.commandObserver

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.Random
import it.unibo.scafi.incarnations.BasicAbstractSpatialSimulationIncarnation
import it.unibo.scafi.space.{Point3D, SpaceHelper}
import com.raquo.laminar.api.L.{*, given}

import scala.scalajs.js.timers.setTimeout

case class EngineImpl(ncols: Int, nrows: Int, ndepth : Int)(stepx: Int, stepy: Int, stepz: Int)(proximityThreshold: Int):

  running.signal.foreach { isRunning =>
    if isRunning then
      executeIterations(0, ids, new Random(), net, Seq.empty)
  }(unsafeWindowOwner)


  private object BasicSpatialIncarnation extends BasicAbstractSpatialSimulationIncarnation:
    override type P = Point3D
    private trait MyDistanceStrategy extends DistanceStrategy
    override def buildNewSpace[E](elems: Iterable[(E, P)]): SPACE[E] =
      new Basic3DSpace(elems.toMap) with MyDistanceStrategy

  private val positions: List[Point3D] = SpaceHelper.grid3DLocations(
    Grid3DSettings(nrows, ncols, ndepth, stepx, stepy, stepz, tolerance = 0)
  )
  private val ids: IndexedSeq[Int] = 1 to ncols * nrows * ndepth
  private val devsToPos: Map[Int, Point3D] = ids.zip(positions).toMap

  import BasicSpatialIncarnation._

  private object Spatial extends AggregateProgram with StandardSensors:
    def main(): Int = rep(0){_ + 1}
  
  
  private val net = new SpaceAwareSimulator(
    space = new Basic3DSpace(devsToPos, proximityThreshold = proximityThreshold),
    devs = devsToPos.map { case (d, p) => d -> new DevInfo(d, p,
      lsns = Map.empty,
      nsns => nbr => null)
    },
    simulationSeed = System.currentTimeMillis(),
    randomSensorSeed = System.currentTimeMillis()
  )

  
  private def executeIterations(
                         current: Int,
                         ids: Seq[Int],
                         simulationRandom: scala.util.Random,
                         net: SpaceAwareSimulator,
                         executedNodes: Seq[Int]
                       ): Unit =
    if running.now() then
      animationObserver.onNext(NextTick())
      
      val nextIdToRun = ids(simulationRandom.nextInt(ids.size))
      val updatedExecutedNodes = executedNodes :+ nextIdToRun
        if current % batch.now() == 0 then
          Future {
            net.exec(Spatial, Spatial.main(), nextIdToRun)
            action(current)
          }.onComplete { _ =>
                js.Dynamic.global.requestAnimationFrame { (_: Double) =>
                      executeIterations(current + 1, ids, simulationRandom, net, updatedExecutedNodes)
                }
          }
        else
          net.exec(Spatial, Spatial.main(), nextIdToRun)
          executeIterations(current + 1, ids, simulationRandom, net, updatedExecutedNodes)

    
  


  private def action(current: Int) : Unit =
    val nodes = net.devs.map { case (id, devInfo) => Node(id, Position(devInfo.pos.x, devInfo.pos.y, devInfo.pos.z), net.`export`(id).map(_.root().toString).getOrElse("."), 0x00ff)}.toSet
    val edges: Set[(Id, Id)] = net.devs.flatMap { case (d, _) => net.neighbourhood(d).map(nbr => (d, nbr))}.filter { case (a, b) => a < b }.toSet
    commandObserver.onNext(SetNodes(nodes))
    commandObserver.onNext(SetEdgesByIds(edges))
    






