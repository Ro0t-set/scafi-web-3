package controller
import model.{ModelModule, Node, SimpleNode}
import org.scalajs.dom
import view.ViewModule

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global




object ControllerModule:
  trait Controller:

    def generateRandomGraph(numberOfNode: Int, numberOfEdge: Int): Unit
    def getNodes: Set[Node]
    def getEdges: Set[(Node, Node)]
    def runSimulation(): Unit
  trait Provider:
    val controller: Controller
  type Requirements = ViewModule.Provider with ModelModule.Provider
  trait Component:
    context: Requirements =>
    class ControllerImpl extends Controller:

      def getNodes: Set[Node] = context.model.getNodes

      def getEdges: Set[(Node, Node)] = context.model.getEdges

      def generateRandomGraph(numberOfNode: Int, numberOfEdge: Int): Unit =

          runSimulation()


      def runSimulation(): Unit = {
        import it.unibo.scafi.config.GridSettings
        import it.unibo.scafi.incarnations.BasicAbstractSpatialSimulationIncarnation
        import it.unibo.scafi.space.{Point2D, SpaceHelper}

        object BasicSpatialIncarnation extends BasicAbstractSpatialSimulationIncarnation:
          override type P = Point2D

          trait MyEuclideanStrategy extends EuclideanStrategy:
            this: Basic3DSpace[_] =>
            override val proximityThreshold = 0.1

          override def buildNewSpace[E](elems: Iterable[(E, P)]): SPACE[E] =
            new Basic3DSpace(elems.toMap) with MyEuclideanStrategy

        import BasicSpatialIncarnation._

        object DemoSpatial extends AggregateProgram with StandardSensors {
          def main(): Int = foldhood(0)(_ + _) {
            1
          }
        }

        val (ncols, nrows) = (10, 10)
        val (stepx, stepy) = (1, 1)
        val positions: List[Point2D] = SpaceHelper.gridLocations(GridSettings(nrows, ncols, stepx, stepy, tolerance = 0))
        val ids: IndexedSeq[Int] = for (i <- 1 to ncols * nrows) yield i
        val devsToPos: Map[Int, Point2D] = ids.zip(positions).toMap
        val net = new SpaceAwareSimulator(
          space = new Basic3DSpace(devsToPos, proximityThreshold = 1.8),
          devs = devsToPos.map { case (d, p) => d -> new DevInfo(d, p,
            lsns = Map.empty,
            nsns => nbr => null)
          },
          simulationSeed = System.currentTimeMillis(),
          randomSensorSeed = System.currentTimeMillis()
        )

        Future {
          var v: Long = java.lang.System.currentTimeMillis()
          net.executeMany(
            node = DemoSpatial,
            size = 100000,
            action = (n, i) => {
              if (i % 10 == 0) {
                println("Simulation step: " + i)
                context.model.setNodes(positions.map(p => SimpleNode(p.toString(), (p.x, p.y, p.z), i.toString, 0xff000)).toSet)
                // Aggiorna la view sul main thread
                dom.window.setTimeout(() => {
                  context.view.setNodes(context.model.getNodes)
                  println("Nodes updated")
                }, 0)

                val newv = java.lang.System.currentTimeMillis()
                v = newv
              }
              if (i > 0 && i % 500 == 0) {
                println("Repositioning device 3 at step " + i)
                net.setPosition(3, new Point2D(0, 0))
              }
            }
          )
        }.recover {
          case e: Throwable => println("Error in simulation: " + e.getMessage)
        }
      }


  trait Interface extends Provider with Component:
    self: Requirements =>