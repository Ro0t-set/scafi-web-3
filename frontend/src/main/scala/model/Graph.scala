package model

type Id = String
type Point3D = (Double, Double, Double)
type Edge = (Node, Node)

trait Node:
  def id: Id
  def position: Point3D
  def label: String
  def color: Int

trait Graph:
  def nodes: Set[Node]
  def edges: Set[Edge]

case class SimpleNode(id: Id, position: Point3D, label: String, color: Int) extends Node
case class SimpleGraph(nodes: Set[Node], edges: Set[Edge]) extends Graph
