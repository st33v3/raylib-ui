package raylib

import geom.Whit

import java.lang.foreign.{MemorySegment, ValueLayout}
import scala.annotation.targetName
import raylib.{=~= => approx}
opaque type Point = Long

object Point:
  def apply(x: Float, y: Float): Point = (java.lang.Float.floatToIntBits(x).toLong & 0xFFFFFFFFL) | (java.lang.Float.floatToIntBits(y).toLong << 32)
  private[raylib] def fromLong(p: Long): Point = p
  def unapply(p: Point): Option[(Float, Float)] = Some((p.x, p.y))
  def fromWhit(w: Whit): Point = Point(w.x.toFloat, w.y.toFloat)

  val layout = ValueLayout.JAVA_LONG
  def put(ptr: MemorySegment, offset: Long, point: Point): Long = 
    ptr.set(layout, offset, point)
    offset + layout.byteSize

  extension (p: Point)
    def x: Float = java.lang.Float.intBitsToFloat(p.toInt)
    def y: Float = java.lang.Float.intBitsToFloat((p >> 32).toInt)
    @targetName("plus")
    def +(size: Size): Point = Point(x + size.w, y + size.h)
    @targetName("minus")
    def -(size: Size): Point = Point(x - size.w, y - size.h)
    @targetName("dist")
    def --(point: Point): Size = Size(x - point.x, y - point.y)
    def str = s"(${p.x}, ${p.y})"
    def sdist(other: Point): Float = (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y)
    def =~=(other: Point, epsilon: Float): Boolean = (x `approx` other.x) && (y `approx` other.y)
    private[raylib] def toLong: Long = p

