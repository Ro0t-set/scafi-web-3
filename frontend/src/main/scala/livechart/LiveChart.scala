package livechart

import scala.scalajs.js
import scala.scalajs.js.annotation.*

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

@main
def LiveChart(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )
end LiveChart

object Main:
  val model = new Model
  import model.*

  def appElement(): Element =
    div(
      h1("ScaFi Web 3"),
      renderDataTable(),
      renderDataChart(),
      renderDataList(),
    )
  end appElement

  def renderDataList(): Element =
    ul(
      children <-- dataSignal.split(_.id) { (id, initial, itemSignal) =>
        li(child.text <-- itemSignal.map(item => s"${item.label}"))
      }
    )
  end renderDataList

  def renderDataTable(): Element =
    table(
      thead(tr(th("Label"), th("Value"), th("X"), th("Y"))),
      tbody(
        children <-- dataSignal.split(_.id) { (id, initial, itemSignal) =>
          renderDataItem(id, itemSignal)
        },
      ),
      tfoot(tr(
        td(button("➕", onClick --> (_ => addNodeItem(NodeItem())))),
        td(),
        td(),
      )),
    )
  end renderDataTable

  def renderDataItem(id: NodeID, itemSignal: Signal[NodeItem]): Element =
    tr(
      td(
        inputForString(
          itemSignal.map(_.label),
          makeNodeItemUpdater(id, { (item, newLabel) =>
            item.copy(label = newLabel)
          })
        )
      ),
      td(
        inputForInt(
          itemSignal.map(_.x),
          makeNodeItemUpdater(id, { (item, newX) =>
            item.copy(x = newX)
          })
        )
      ),
      td(
        inputForInt(
          itemSignal.map(_.y),
          makeNodeItemUpdater(id, { (item, newY) =>
            item.copy(y = newY)
          })
        )
      ),
      td(button("🗑️", onClick --> (_ => removeNodeItem(id)))),
    )
  end renderDataItem

  def inputForString(valueSignal: Signal[String],
      valueUpdater: Observer[String]): Input =
    input(
      typ := "text",
      value <-- valueSignal,
      onInput.mapToValue --> valueUpdater,
    )
  end inputForString

  def inputForDouble(valueSignal: Signal[Double],
      valueUpdater: Observer[Double]): Input =
    val strValue = Var[String]("")
    input(
      typ := "text",
      value <-- strValue.signal,
      onInput.mapToValue --> strValue,
      valueSignal --> strValue.updater[Double] { (prevStr, newValue) =>
        if prevStr.toDoubleOption.contains(newValue) then prevStr
        else newValue.toString
      },
      strValue.signal --> { valueStr =>
        valueStr.toDoubleOption.foreach(valueUpdater.onNext)
      },
    )
  end inputForDouble

  def inputForInt(valueSignal: Signal[Int],
      valueUpdater: Observer[Int]): Input =
    input(
      typ := "text",
      controlled(
        value <-- valueSignal.map(_.toString),
        onInput.mapToValue.map(_.toIntOption).collect {
          case Some(newCount) => newCount
        } --> valueUpdater,
      ),
    )
  end inputForInt

  /** Chart.js configuration for the bubble chart. */
  private val chartConfig =
    import typings.chartJs.mod.*
    new ChartConfiguration {
  `type` = ChartType.bubble
    data = new ChartData {
      datasets = js.Array(
        new ChartDataSets {
          label = "Dataset"
          backgroundColor = "red"
          data = js.Array(new ChartPoint {
            x = 1
            y = 1
            r = 10
          })
        },
      )
    }
  options = new ChartOptions {
    scales = new ChartScales {
      yAxes = js.Array(new CommonAxe {
        ticks = new TickOptions {
          beginAtZero = true
        }
      })
    }
  }
}
  end chartConfig

  private def renderDataChart(): Element =
    import scala.scalajs.js.JSConverters.*
    import typings.chartJs.mod.*

    var optChart: Option[Chart] = None

    canvasTag(
      // Regular properties of the canvas
      width := "100%",
      height := "500px",

      // onMountUnmount callback to bridge the Laminar world and the Chart.js world
      onMountUnmountCallback(
        // on mount, create the `Chart` instance and store it in optChart
        mount = { nodeCtx =>
          val domCanvas: dom.HTMLCanvasElement = nodeCtx.thisNode.ref
          val chart = Chart.apply.newInstance2(domCanvas, chartConfig)
          optChart = Some(chart)
        },
        // on unmount, destroy the `Chart` instance
        unmount = { thisNode =>
          for (chart <- optChart)
            chart.destroy()
          optChart = None
        }
      ),

      // Bridge the FRP world of dataSignal to the imperative world of the `chart.data`
      dataSignal --> { newData =>
        for (chart <- optChart) {
          chart.data.datasets = js.Array(
            new ChartDataSets {
              backgroundColor = newData.map(_ => "rgba(75, 192, 192, 0.4)").toJSArray // Colore dinamico
              data = newData.map { item =>
                new ChartPoint {
                  x = item.x.toDouble
                  y = item.y.toDouble
                  r = 10 // Dimensione fissa
                }
              }.toJSArray
            },
          )
          chart.update()
        }
      },
    )
  end renderDataChart
end Main
