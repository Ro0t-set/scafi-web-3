package view.player

import API.GraphAPI
import domain.NextTickAdd
import com.raquo.laminar.api.L.*
import org.scalajs.dom.console
import state.AnimationState

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.JSON
import state.AnimationState.{animationObserver, batch, engine, running}
import typings.std.global.setTimeout
import view.graph.scene.ThreeSceneImpl

import scala.concurrent.Future

/** Represents an engine controller for handling network data and animations.
  * @tparam N
  *   Type of network nodes
  * @tparam E
  *   Type of network edges
  */
trait EngineController[N, E]:
  type JsonNetwork = (N, E)

  /** Starts the animation processing loop */
  def start(): Unit

  /** Load first frame */
  def loadNextFrame(): Unit

  /** Processes the next batch of network data
    * @return
    *   A tuple of nodes and edges
    */
  def processNextBatch(): JsonNetwork

  /** Handles new network data
    * @param net
    *   Tuple of nodes and edges to process
    */
  def handleNewData(net: JsonNetwork): Unit

/** Default implementation of the EngineController for JavaScript dynamic types
  */
object EngineController:
  opaque type DynamicNetwork = (js.Dynamic, js.Dynamic)

  class Player extends EngineController[js.Dynamic, js.Dynamic]:

    private def getEngineOrEmpty: js.Dynamic =
      engine.now().getOrElse {
        console.error("Engine not found")
        js.Dynamic.literal()
      }

    override def loadNextFrame(): Unit =
      handleNewData((getEngineOrEmpty.getNodes(), getEngineOrEmpty.getEdges()))

    override def processNextBatch(): JsonNetwork =
      val currentEngine = getEngineOrEmpty
      getEngineOrEmpty.executeIterations()
      (currentEngine.getNodes(), currentEngine.getEdges())

    override def handleNewData(net: JsonNetwork): Unit = net match
      case (nodes, edges) =>
        for
          nodesJson <- Option(JSON.stringify(nodes))
          edgesJson <- Option(JSON.stringify(edges))
        do
          GraphAPI.addNodesFromJson(nodesJson)
          GraphAPI.addEdgesFromJson(edgesJson)

    override def start(): Unit =
      def loop(): Unit =
        if running.now() then
          val batchCount = batch.now()
          (1 until batchCount).foreach { _ =>
            getEngineOrEmpty.executeIterations()
          }

          animationObserver.onNext(NextTickAdd(batchCount + 1))
          handleNewData(processNextBatch())
          setTimeout(() => loop(), 16)

      loop()
