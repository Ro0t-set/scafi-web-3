package view.graph.adapter
import typings.three.mod._
import typings.three.srcCoreObject3DMod.Object3D
import typings.three.srcCoreObject3DMod.Object3DEventMap

import scala.scalajs.js

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

  def unsafeCast[T](obj: js.Any): T = obj.asInstanceOf[T]

  object ThreeCamera:
    def unapply(cam: PerspectiveCamera): Option[ThreeCamera] = cam.`type` match
      case "PerspectiveCamera" => Some(cam.asInstanceOf[ThreeCamera])
      case "Camera" => Some(cam.asInstanceOf[ThreeCamera])
      case _                   => None

  object GenericObject3D:
    def unapply(obj: Object3D[?]): Option[GenericObject3D] =
      Some(obj.asInstanceOf[GenericObject3D])

  object Group:
    def unapply(obj: GenericObject3D): Option[ThreeGroup] = obj.`type` match
      case "Group" => Some(obj.asInstanceOf[ThreeGroup])
      case _       => None

  object Line:
    def unapply(obj: GenericObject3D): Option[ThreeLine] = obj.`type` match
      case "Line" => Some(obj.asInstanceOf[ThreeLine])
      case _      => None

  object Points:
    def unapply(obj: GenericObject3D): Option[ThreePoints] = obj.`type` match
      case "Points" => Some(obj.asInstanceOf[ThreePoints])
      case _        => None

  object Sprite:
    def unapply(obj: GenericObject3D): Option[ThreeSprite] = obj.`type` match
      case "Sprite" => Some(obj.asInstanceOf[ThreeSprite])
      case _        => None

  object Mesh:
    def unapply(obj: GenericObject3D): Option[ThreeMesh] = obj.`type` match
      case "Mesh" => Some(obj.asInstanceOf[ThreeMesh])
      case _      => None
