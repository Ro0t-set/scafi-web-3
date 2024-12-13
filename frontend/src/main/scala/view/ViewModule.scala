package view

import com.raquo.laminar.api.L.{*, given}
import controller.ControllerModule
import org.scalajs.dom
import com.raquo.airstream.ownership.Owner

object ViewModule:
  trait View:
    def renderTitle(): Element
    def renderNodeGeneratorForm(): Element
    def renderPage(): Unit

  trait Provider:
    val view: View

  private type Requirements = ControllerModule.Provider

  trait Component:
    context: Requirements =>

    class ViewImpl extends View:
      private val nodeCount = Var(0)
      val scene = ThreeSceneImpl(500, 500, 800)

      def renderTitle(): Element = h1("ScaFi Web 3")

      def renderNodeGeneratorForm(): Element =
        val numberOfNodesInput = input(
          typ := "number",
          placeholder := "Number of nodes",
          required := true
        )
        val numberOfEdgesInput = input(
          typ := "number",
          placeholder := "Number of edges",
          required := true
        )
        val generateButton = button("Generate")
        div(
          h2("Node Generator"),
          form(
            onSubmit.preventDefault --> { _ =>
              val numberOfNodes = numberOfNodesInput.ref.value.toInt
              val numberOfEdges = numberOfEdgesInput.ref.value.toInt
              context.controller.generateRandomGraph(numberOfNodes, numberOfEdges)
            },
            numberOfNodesInput,
            numberOfEdgesInput,
            generateButton
          ),
          h3("Number of nodes: ", child.text <-- nodeCount.signal)
        )

      def renderPage(): Unit =
        renderOnDomContentLoaded(
          dom.document.getElementById("app"),
          div(
            renderTitle(),
            renderNodeGeneratorForm(),
            scene.renderScene(),
            // Assicurati che il Signal venga osservato
            onMountCallback { ctx =>
              implicit val owner: Owner = ctx.owner
              context.controller.getNodes.foreach { nodes =>
                nodeCount.set(nodes.size)
                scene.setNodes(nodes)
                println(nodes.size)
              }
            }
          )
        )

  trait Interface extends Provider with Component:
    self: Requirements =>