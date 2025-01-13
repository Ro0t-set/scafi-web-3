package view.graph.adapter

import typings.three.mod.*
import typings.three.srcCoreObject3DMod.Object3DEventMap
import typings.three.srcRenderersWebGLRendererMod.WebGLRendererParameters
import typings.three.examplesJsmControlsOrbitControlsMod.OrbitControls
import org.scalajs.dom
import org.scalajs.dom.HTMLElement
import ThreeJsAdapter.SceneWrapper
import typings.three.examplesJsmAddonsMod.CSS2DRenderer

@SuppressWarnings(Array("org.wartremover.warts.All"))
object ThreeJsAdapter:
  type Object3DType = Object3D[Object3DEventMap]

  extension (obj: Object3D[?])
    def asThreeObject: Object3DType =
      obj.asInstanceOf[Object3DType]

  extension (camera: PerspectiveCamera)
    def asCamera: Camera =
      camera.asInstanceOf[Camera]

  extension (obj: PerspectiveCamera)
    def asObject3D: Object3D[?] =
      obj.asInstanceOf[Object3D[?]]

  class SceneWrapper(val underlying: Scene):
    def addObject(obj: Object3D[?]): Unit =
      underlying.add(obj.asThreeObject)

    def removeObject(obj: Object3D[?]): Unit =
      obj.asInstanceOf[Group[Object3DEventMap]].children.foreach { child =>
        val typedChild = child.asInstanceOf[Sprite[Object3DEventMap]]
        typedChild.geometry.dispose()
        if typedChild.material.map != null then
          typedChild.material.map.dispose()
        typedChild.material.dispose()
      }
      underlying.remove(obj.asThreeObject)

    def findByName(name: String): Option[Object3DType] =
      Option(underlying.getObjectByName(name)) match
        case Some(obj: Object3D[?]) => Some(obj.asThreeObject)
        case _                      => None

  object CameraFactory:
    def createPerspectiveCamera(
        fov: Double,
        aspect: Double,
        near: Double,
        far: Double
    ): PerspectiveCamera =
      PerspectiveCamera(fov, aspect, near, far)

  object RendererFactory:
    def createWebGLRenderer(): WebGLRenderer =
      val params = WebGLRendererParameters()
      params.precision = "lowp"
      WebGLRenderer(params)

    def createCSS2DRenderer(): CSS2DRenderer = CSS2DRenderer()

  object ControlsFactory:
    def createOrbitControls(
        camera: PerspectiveCamera,
        domElement: HTMLElement
    ): OrbitControls =
      OrbitControls(camera.asCamera, domElement)

  object VectorUtils:
    def setPosition(
        obj: PerspectiveCamera,
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
  def apply(): SceneWrapper = new SceneWrapper(Scene())
