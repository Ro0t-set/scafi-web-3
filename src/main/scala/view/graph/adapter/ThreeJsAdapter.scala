package view.graph.adapter

import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import typings.three.examplesJsmAddonsMod.CSS2DRenderer
import typings.three.examplesJsmControlsOrbitControlsMod.OrbitControls
import typings.three.mod._
import typings.three.srcCoreObject3DMod.Object3DEventMap
import typings.three.srcRenderersWebGLRendererMod.WebGLRendererParameters

object ThreeJsAdapter:
  class SceneWrapper(val underlying: Scene):
    def addObject(obj: GenericObject3D): Unit =
      underlying.add(obj)

    def removeObject(obj: GenericObject3D): Unit =
      import ThreeType._

      obj match
        case Group(group) =>
          for
            child <- group.children
            _ <- child match
              case Line(line) =>
                line.geometry.dispose()
                line.material.dispose()
                underlying.remove(line)
              case Points(points) =>
                points.geometry.dispose()
                points.material.dispose()
                underlying.remove(points)
              case Sprite(sprite) =>
                sprite.geometry.dispose()
                sprite.material.dispose()
                sprite.material.map.dispose()
                underlying.remove(sprite)
              case _ => ()
          do underlying.remove(group)
        case _ => ()

    def findByName(name: String): Option[GenericObject3D] =
      import ThreeType._
      underlying.getObjectByName(name) match
        case GenericObject3D(obj) => Some(obj)
        case _                    => None

  object CameraFactory:
    def createPerspectiveCamera(
        fov: Double,
        aspect: Double,
        near: Double,
        far: Double
    ): Camera =
      val cam = PerspectiveCamera(fov, aspect, near, far)
      ThreeType.unsafeCast[ThreeCamera](cam)

  object RendererFactory:
    def createWebGLRenderer(): WebGLRenderer =
      val params = WebGLRendererParameters()
      params.precision = "lowp"
      WebGLRenderer(params)

    def createCSS2DRenderer(): CSS2DRenderer = CSS2DRenderer()

  object ControlsFactory:
    def createOrbitControls(
        camera: Camera,
        domElement: HTMLElement
    ): OrbitControls =
      OrbitControls(camera, domElement)

  object VectorUtils:
    def setPosition(
        obj: ThreeCamera,
        x: Double,
        y: Double,
        z: Double
    ): Unit =
      obj.position.set(x, y, z)

    def setTarget(
        controls: OrbitControls,
        x: Double,
        y: Double,
        z: Double
    ): Unit =
      controls.target.set(x, y, z)

object SceneWrapper:
  def apply(): ThreeJsAdapter.SceneWrapper =
    new ThreeJsAdapter.SceneWrapper(Scene())
