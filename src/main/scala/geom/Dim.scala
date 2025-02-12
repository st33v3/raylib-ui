package geom

import scala.annotation.targetName

case class Dim(w: Double, h: Double):
  @targetName("plus")
  def +(that: Dim): Dim = Dim(w + that.w, h + that.h)
  @targetName("mult")
  def *(scale: Double): Dim = Dim(w * scale, h * scale)
  @targetName("div")
  def /(scale: Double): Dim = Dim(w / scale, h / scale)
  infix def dot(that: Dim): Double = w * that.w + h * that.h
  infix def cross(that: Dim): Double = w * that.h - h * that.w
  def =~=(that: Dim): Boolean = w =~= that.w && h =~= that.h
  def asWhit: Whit = Whit(w, h)