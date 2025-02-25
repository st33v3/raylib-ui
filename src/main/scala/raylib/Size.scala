package raylib

import geom.Dim
import raylib.Point
import raylib.Point.layout

import java.lang.foreign.{MemorySegment, ValueLayout}
import scala.annotation.targetName

opaque type Size = Long

object Size:
  def apply(w: Float, h: Float): Size = (java.lang.Float.floatToIntBits(w).toLong & 0xFFFFFFFFL) | (java.lang.Float.floatToIntBits(h).toLong << 32)
  def unapply(p: Size): Option[(Float, Float)] = Some((p.w, p.h))
  def fromDim(d: Dim): Size = Size(d.w.toFloat, d.h.toFloat)

  val layout = ValueLayout.JAVA_LONG
  def put(ptr: MemorySegment, offset: Long, size: Size): Long = 
    ptr.set(layout, offset, size)
    offset + layout.byteSize

  extension (p: Size)
    def w: Float = java.lang.Float.intBitsToFloat(p.toInt)
    def h: Float = java.lang.Float.intBitsToFloat((p >>> 32).toInt)
    @targetName("plus")
    def +(size: Size): Size = Size(w + size.w, h + size.h)
    @targetName("minus")
    def -(size: Size): Size = Size(w - size.w, h - size.h)
    @targetName("mult")
    def *(f: Float): Size = Size(w * f, h * f)
    @targetName("div")
    def /(f: Float): Size = Size(w / f, h / f)
    def half: Size = Size(w / 2, h / 2)
    def str = s"(${p.w} x ${p.h})"
    def asPoint: Point = Point.fromLong(p)
    infix def dot(other: Size): Float = w * other.w + h * other.h
    infix def cross(other: Size): Float = w * other.h - h * other.w