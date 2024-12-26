package view

import scala.scalajs.js
import scala.scalajs.js.annotation._
import scala.scalajs.js.Dynamic.{global => g}
import org.scalajs.dom
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.JSON

// Rappresenta il risultato di nextAndGetJsonNetwork
@js.native
trait JsNetworkData extends js.Object:
  val ju_concurrent_atomic_AtomicReference__f_value: AtomicReference = js.native

@js.native
trait AtomicReference extends js.Object:
  val s_util_Success__f_value: js.Any = js.native

// Accesso a ClientMain nel contesto globale
@js.native
@JSGlobal("scastie.ClientMain")
object ClientMain extends js.Object:
  var signal: js.Function3[js.Any, js.Any, js.Any, Unit] = js.native

// Funzione per inizializzare il motore
def initializeEngine(): Unit =
  println("Engine initializing...")

  // Inizializza l'istanza di EngineImpl dallo scope globale
  val engine = js.Dynamic.global.EngineImpl(10, 20, 3, 100, 100, 100, 190)
  API.addNodeFromJson(engine.nextAndGetJsonNetwork())

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

      // Inizializza il motore al ricevimento del segnale
      initializeEngine()

      // Richiama il segnale originale
      originalSignal(result, attachedElements, scastieId)

// Funzione principale per inizializzare tutto
def init(): Unit =
  interceptSignal()
