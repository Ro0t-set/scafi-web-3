package model

type Id = String
type Point3D = (Double, Double, Double)
type Edge = (Node, Node)

trait Node:
  def id: Id
  def position: Point3D
  def label: String
  def color: String

trait Graph:
  def nodes: Set[Node]
  def edges: Set[Edge]
