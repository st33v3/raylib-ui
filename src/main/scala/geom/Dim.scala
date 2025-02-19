package geom

import scala.annotation.targetName

case class Dim(w: Double, h: Double):
  @targetName("plus")
  def +(that: Dim): Dim = Dim(w + that.w, h + that.h)
  @targetName("minus")
  def -(that: Dim): Dim = Dim(w - that.w, h - that.h)
  @targetName("mult")
  def *(scale: Double): Dim = Dim(w * scale, h * scale)
  @targetName("div")
  def /(scale: Double): Dim = Dim(w / scale, h / scale)
  @targetName("negate")
  def unary_- : Dim = Dim(-w, -h)
  infix def dot(that: Dim): Double = w * that.w + h * that.h
  infix def cross(that: Dim): Double = w * that.h - h * that.w
  def =~=(that: Dim): Boolean = w =~= that.w && h =~= that.h
  def tangent: Dim = Dim(-h, w)
  def angle: Double = Math.atan2(h, w)
  def asWhit: Whit = Whit(w, h)

  def unit: Dim =
    if length =~= 0.0 then throw new Exception("Cannot normalize zero-length vector")
    this / length

  def slength: Double = w * w + h * h
  lazy val length: Double = Math.sqrt(slength)

object Dim:
  val zero: Dim = Dim(0, 0)
  val unitX: Dim = Dim(1, 0)
  val unitY: Dim = Dim(0, 1)