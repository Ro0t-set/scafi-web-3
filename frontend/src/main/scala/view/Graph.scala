package view

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom
import typings.visNetwork.mod.*
import typings.visData.mod.*
import typings.visNetwork.declarationsNetworkGephiParserMod.*
import typings.visNetwork.declarationsNetworkNetworkMod.*
import typings.visNetwork.declarationsNetworkNetworkMod.Node
import typings.visNetwork.declarationsNetworkNetworkMod.DataInterfaceNodes

import typings.visNetwork.declarationsNetworkNetworkMod.Edge
import typings.visNetwork.declarationsNetworkNetworkMod.DataInterfaceEdges


@main
def LiveGraph(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )

object Main:
  def appElement(): Element =
    div(
      h1("ScaFi Web 3"),
      renderDataGraph()
    )

  private def renderDataGraph(): Element =
    import scala.scalajs.js.JSConverters.*
    import scala.util.Random

    // Genera 100 nodi casuali
    val randomNodes = (1 to 100).map { mid =>
      new Node {
        this.id = mid
        this.label = s"Node $mid"
        this.x = Random.nextInt(1000) - 500 // Coordinate X casuali
        this.y = Random.nextInt(1000) - 500 // Coordinate Y casuali
        this.color = if (Random.nextBoolean()) "#ff0000" else "#00ff00" // Colore casuale
        this.fixed = true
      }
    }.toJSArray

    // Genera collegamenti casuali tra i nodi
    val randomEdges = (1 to 200).map { _ =>
      new Edge {
        this.from = Random.nextInt(100) + 1 // Nodo sorgente casuale
        this.to = Random.nextInt(100) + 1   // Nodo destinazione casuale
      }
    }.toJSArray

    // Dati finali con nodi ed archi
    val data = new Data {
      edges = randomEdges
      nodes = randomNodes
    }

    val options = new Options {
      nodes = new NodeOptions {
        shape = "dot"
        size = 10
        font= "12px arial red"
        color = "red"
      }
      physics = false
    }

    // Funzione per aggiornare le etichette
    def updateLabels(network: Network_): Unit =
      dom.window.setInterval(() => {
        val updatedNodes = randomNodes.map { node =>
          node.label = s"Random ${Random.nextInt(1000)}"
          node
        }
        network.setData(new Data {
          nodes = updatedNodes
          edges = randomEdges
        })
      }, 20)

    // Crea il contenitore e configura la callback onMount
    div(
      idAttr := "mynetwork",
      width := "100%",
      height := "1000px",
      // Quando l'elemento viene montato, possiamo creare la rete
      onMountCallback { nodeCtx =>
        val container = nodeCtx.thisNode.ref.asInstanceOf[dom.html.Element]
        val network = new Network_(container, data, options)

        // Inizia l'aggiornamento continuo delle etichette
        updateLabels(network)
      }
    )
  end renderDataGraph
end Main