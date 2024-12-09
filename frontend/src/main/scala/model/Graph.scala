package model


import scala.language.implicitConversions
import scala.scalajs.js
/** A graph model used to represent an aggregate system. Conceptually this data structure should be immutable. This
 * graph contains a set of nodes uniquely identified by an id. Each node has a set of labels that decorate a node with
 * some useful information (e.g. sensor value,...). The graph has also a set of vertex that link nodes with each other.
 */
trait Graph:
  def contains(id: String): Boolean
  def apply(id: String): Node
  def get(id: String): Option[Node]
  def nodes: Set[Node]
  def vertices: Set[Vertex]
  def neighbours(id: String): Set[Node]
  def neighbours(node: Node): Set[Node]


object Graph {
  implicit def tupleToVertex(tuple: (String, String)): Vertex = Vertex(tuple._1, tuple._2)
  def empty: Graph = NaiveGraph(Set(), Set())
}
case class Vertex(from: String, to: String)

case class Point3D(x: Double, y: Double, z: Double)

case class Node(id: String, position: Point3D, labels: Map[String, Any] = Map.empty):
  def canEqual(other: Any): Boolean = other.isInstanceOf[Node]

  override def equals(other: Any): Boolean = other match
    case that: Node =>
      (that canEqual this) &&
        id == that.id
    case _ => false


  override def hashCode(): Int =
    val state = Seq(id)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)

  override def toString: String = s"Node($id, $position, $labels)"
