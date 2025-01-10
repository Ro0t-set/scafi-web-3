package view.player

import API.GraphAPI
import domain.NextTick
import com.raquo.laminar.api.L.*
import org.scalajs.dom.window.console
import state.AnimationState

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.timers.setTimeout
import state.AnimationState.{animationObserver, batch, engine, engineSignal, running, runningSignal}

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

  class Impl extends EngineController[js.Dynamic, js.Dynamic]:
    runningSignal.foreach {
      case true  => start()
      case false => ()
    }(unsafeWindowOwner)

    engineSignal.foreach {
      case Some(_) => loadNextFrame()
      case None    => ()
    }(unsafeWindowOwner)

    private def getEngineOrEmpty: js.Dynamic =
      engine.now().getOrElse(js.Dynamic.literal())

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
        if running.signal.now() then
          val batchCount = batch.signal.now()
          (1 until batchCount).foreach { _ =>
            animationObserver.onNext(NextTick())
            getEngineOrEmpty.executeIterations()
          }
          animationObserver.onNext(NextTick())
          handleNewData(processNextBatch())
          setTimeout(0)(loop())

      loop()

  given EngineController[js.Dynamic, js.Dynamic] = Impl()
