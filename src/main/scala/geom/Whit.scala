package geom

import scala.annotation.targetName

case class Whit(x: Double, y: Double):
  @targetName("diff")
  def --(that: Whit): Dim = Dim(that.x - x, that.y - y)
  @targetName("plus")
  def +(that: Dim): Whit = Whit(x + that.w, y + that.h)
  @targetName("minus")
  def -(that: Dim): Whit = Whit(x - that.w, y - that.h)
  def sdist(that: Whit): Double = (x - that.x) * (x - that.x) + (y - that.y) * (y - that.y)
  def =~=(that: Whit): Boolean = x =~= that.x && y =~= that.y
  def asDim: Dim = Dim(x, y)