package raylib

import raylib.{Point, Size}

import java.lang.foreign.{MemoryLayout, MemorySegment}

case class Rect(start: Point, size: Size):
  assert(size.w >= 0 && size.h >= 0)
  def x: Float = start.x
  def y: Float = start.y
  def w: Float = size.w
  def h: Float = size.h
  def end: Point = Point(x + w, y + h)
  def center: Point = Point(x + w / 2, y + h / 2)
  def fromCenter(w: Float, h: Float): Rect = Rect(center - Size(w / 2, h / 2), Size(w, h))
  def fromCenter(size: Size): Rect = fromCenter(size.w, size.h)
  def intersects(other: Rect): Boolean =
    x < other.x + other.w && x + w > other.x && y < other.y + other.h && y + h > other.y
  def contains(p: Point): Boolean = x <= p.x && p.x <= x + w && y <= p.y && p.y <= y + h
  override def toString = s"[${start.str}, ${size.str}]"
  def union(other: Rect): Rect =
    val start = Point(x min other.x, y min other.y)
    val e1 = this.end
    val e2 = other.end
    val end = Point(e1.x max e2.x, e1.y max e2.y)
    Rect(start, end -- start)

object Rect:
  def apply(x: Float, y: Float, w: Float, h: Float): Rect = new Rect(Point(x, y), Size(w, h))
  def apply(start: Point, size: Size): Rect = new Rect(start, size)
  def fromCenter(center: Point, size: Size): Rect = Rect(center - size / 2, size)

  val layout = MemoryLayout.structLayout(Point.layout, Size.layout)
  def put(ptr: MemorySegment, offset0: Long, rect: Rect): Long =
    var offset = offset0
    offset = Point.put(ptr, offset, rect.start)
    offset = Size.put(ptr, offset, rect.size)
    offset
