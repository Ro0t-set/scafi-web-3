package view.graph.adapter
import typings.three.mod._
import typings.three.srcCoreObject3DMod.Object3D
import typings.three.srcCoreObject3DMod.Object3DEventMap

type GenericObject3D = Object3D[Object3DEventMap]
type ThreeGroup      = Group[Object3DEventMap]
type ThreePoints =
  Points[BufferGeometry[Nothing], PointsMaterial, Object3DEventMap]
type ThreeSprite = Sprite[Object3DEventMap]
type ThreeLine =
  Line[BufferGeometry[Nothing], LineBasicMaterial, Object3DEventMap]
type ThreeMesh   = Mesh[BufferGeometry[Nothing], Material, Object3DEventMap]
type ThreeCamera = Camera

@SuppressWarnings(Array("org.wartremover.warts.All"))
object ThreeType:

  object TypePatterns:
    def unapply(obj: Object3D[?]): Option[String] = Some(obj.`type`)

  object ThreeCamera:
    def unapply(cam: PerspectiveCamera): Option[ThreeCamera] =
      if (cam.`type` == "PerspectiveCamera") Some(cam.asInstanceOf[ThreeCamera])
      else None

  object GenericObject3D:
    def unapply(obj: { def `type`: String }): Option[GenericObject3D] =
      Some(obj.asInstanceOf[GenericObject3D])

  object Group:
    def unapply(obj: GenericObject3D): Option[ThreeGroup] =
      if (obj.`type` == "Group") Some(obj.asInstanceOf[ThreeGroup]) else None

  object Line:
    def unapply(obj: GenericObject3D): Option[ThreeLine] =
      if (obj.`type` == "Line") Some(obj.asInstanceOf[ThreeLine]) else None

  object Points:
    def unapply(obj: GenericObject3D): Option[ThreePoints] =
      if (obj.`type` == "Points") Some(obj.asInstanceOf[ThreePoints]) else None

  object Sprite:
    def unapply(obj: GenericObject3D): Option[ThreeSprite] =
      if (obj.`type` == "Sprite") Some(obj.asInstanceOf[ThreeSprite]) else None

  object Mesh:
    def unapply(obj: GenericObject3D): Option[ThreeMesh] =
      if (obj.`type` == "Mesh") Some(obj.asInstanceOf[ThreeMesh]) else None
