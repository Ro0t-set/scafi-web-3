package view.player

import API.GraphAPI
import org.scalajs.dom
import scala.scalajs.js
import scala.scalajs.js.annotation.*
import scala.scalajs.js.JSON
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

@js.native
@SuppressWarnings(Array("org.wartremover.warts.All"))
@JSGlobal("scastie.ClientMain")
object ClientMain extends js.Object:
  var signal: js.Function3[js.Any, js.Any, js.Any, Unit] = js.native

@SuppressWarnings(Array("org.wartremover.warts.All"))
def startAsyncNodeGeneration(engine: js.Dynamic): Unit =
  def loop(): Unit =
    val nodes     = engine.nextAndGetJsonNetwork()
    val nodesJson = JSON.stringify(nodes)
    GraphAPI.addNodesFromJson(nodesJson)
    dom.window.setTimeout(() => loop(), 0)

  loop()

def initializeEngineAsync(): Unit =
  println("Engine initializing (async)...")
  val engine = js.Dynamic.global.EngineImpl(10, 10, 3, 100, 100, 100, 190)
  startAsyncNodeGeneration(engine)

def interceptSignal(): Unit =
  val originalSignal = ClientMain.signal
  ClientMain.signal =
    (result: js.Any, attachedElements: js.Any, scastieId: js.Any) =>
      initializeEngineAsync()
      originalSignal(result, attachedElements, scastieId)

@JSExportTopLevel("init")
def init(): Unit =
  interceptSignal()
