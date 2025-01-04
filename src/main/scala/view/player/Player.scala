package view.player

import API.GraphAPI

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import scala.scalajs.js.Dynamic.global as g
import org.scalajs.dom
import scala.scalajs.js.JSON

@js.native
trait JsNetworkData extends js.Object:
  val ju_concurrent_atomic_AtomicReference__f_value: AtomicReference = js.native

@js.native
trait AtomicReference extends js.Object:
  val s_util_Success__f_value: js.Any = js.native

@js.native
@JSGlobal("scastie.ClientMain")
@SuppressWarnings(Array("org.wartremover.warts.All"))
object ClientMain extends js.Object:
  var signal: js.Function3[js.Any, js.Any, js.Any, Unit] = js.native

// Funzione per inizializzare il motore
def initializeEngine(): Unit =
  println("Engine initializing...")

  val engine = js.Dynamic.global.EngineImpl(10, 20, 3, 100, 100, 100, 190)
  GraphAPI.addNodesFromJson(JSON.stringify(engine.nextAndGetJsonNetwork()))

def interceptSignal(): Unit =
  val originalSignal = ClientMain.signal

  ClientMain.signal =
    (result: js.Any, attachedElements: js.Any, scastieId: js.Any) =>
      dom.console.log(
        "Signal intercepted!",
        js.Dynamic.literal(
          "result"           -> result,
          "attachedElements" -> attachedElements,
          "scastieId"        -> scastieId
        )
      )

      initializeEngine()
      originalSignal(result, attachedElements, scastieId)

def init(): Unit =
  interceptSignal()
