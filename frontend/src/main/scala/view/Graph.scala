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
def LiveChart(): Unit =
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

    // Define the nodes
    val nodesData = js.Array[Node](
     new Node {
        id = 1
        label = "Node 1"
        x = 10
        y = 10
        fixed = true
      },
      new Node {
        id = 2
        label = "Node 2"
        x = 100
        y = 200
        fixed = true
      },
      new Node {
        id = 3
        label = "Node 3"
        x =  200
        y = 300
        fixed = true
      },
      new Node {
        id = 4
        label = "Node 4"
        x = 130
        y = 400
        fixed = true
      },
      new Node {
        id = 5
        label = "Node 5"
        x = 140
        y = 500
        fixed = true
     }
    )

    // Define the edges
    val edgesData = js.Array[Edge](
      new Edge {
        from = 1
        to = 3
      },
      new Edge {
        from = 1
        to = 2
      },
      new Edge {
        from = 2
        to = 4
      },
      new Edge {
        from = 2
        to = 5
      }
    )


//    var edges: js.UndefOr[js.Array[Edge] | DataInterfaceEdges] = js.undefined
//    var nodes: js.UndefOr[js.Array[Node] | DataInterfaceNodes] = js.undefined

    val data = new Data {
      edges = edgesData
      nodes = nodesData
    }

    val options = new Options {}

    // Create the container div and set up the onMount callback
    div(
      idAttr := "mynetwork",
      width := "100%",
      height := "500px",
      // When the element is mounted, we can safely instantiate the network
      onMountCallback { nodeCtx =>
        val container = nodeCtx.thisNode.ref.asInstanceOf[dom.html.Element]
        val network = new Network_(container, data, options)
      }
    )
  end renderDataGraph
end Main