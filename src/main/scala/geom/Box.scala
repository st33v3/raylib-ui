package geom

import geom.Dir
import geom.Segment.Quad

case class Box(x: Double, y: Double, w: Double, h: Double):
  assert(w >= 0 && h >= 0, s"Invalid Box($x, $y, $w, $h)")
  lazy val start: Whit = Whit(x, y)
  lazy val end: Whit = Whit(x + w, y + h)
  lazy val center: Whit = Whit(x + w / 2, y + h / 2)
  lazy val size: Dim = Dim(w, h)
  def top: Double = y
  def bottom: Double = y + h
  def left: Double = x
  def right: Double = x + w

  def modify[M](modifier: M)(using bm: BoxModifier[M]): Box = bm.modify(this, modifier)

  def contains(p: Whit): Boolean = p.x >= x && p.x <= x + w && p.y >= y && p.y <= y + h
  def intersects(other: Box): Boolean = x < other.x + other.w && x + w > other.x && y < other.y + other.h && y + h > other.y

  def sdist(p: Whit): Double =
    val dx = (p.x max x min (x + w)) - p.x
    val dy = (p.y max y min (y + h)) - p.y
    dx * dx + dy * dy

  def maxSdist(point: Whit): Double =
    val dx = (point.x - x).abs max (point.x - (x + w)).abs
    val dy = (point.y - y).abs max (point.y - (y + h)).abs
    dx * dx + dy * dy

  def =~=(other: Box): Boolean =
    x =~= other.x && y =~= other.y && w =~= other.w && h =~= other.h

  def union(other: Box): Box =
    val x1 = x min other.x
    val y1 = y min other.y
    val x2 = (x + w) max (other.x + other.w)
    val y2 = (y + h) max (other.y + other.h)
    Box.fromCorners(x1, y1, x2, y2)

  def coordinate(dir: Dir): Double =
    dir.assertMain()
    dir match
      case Dir.N => top
      case Dir.E => right
      case Dir.S => bottom
      case Dir.W => left

  def point(dir: Dir): Whit =
    import Dir.*
    dir match
      case N => Whit(center.x, top)
      case NE => Whit(right, top)
      case E => Whit(right, center.y)
      case SE =>  end
      case S => Whit(center.x, bottom)
      case SW => Whit(left, bottom)
      case W => Whit(left, center.y)
      case NW => start
      case CENTER => center

object Box:
  def apply(x: Double, y: Double, w: Double, h: Double): Box = new Box(x, y, w, h)
  def apply(s: Whit, d: Dim): Box = new Box(s.x, s.y, d.w, d.h)

  def fromCorners(start: Whit, end: Whit): Box = fromCorners(start.x, start.y, end.x, end.y)

  def fromCorners(sx: Double, sy: Double, ex: Double, ey: Double): Box =
    val x = sx min ex
    val y = sy min ey
    val w = (sx max ex) - x
    val h = (sy max ey) - y
    Box(x, y, w, h)

  def fromCenter(center: Whit, size: Dim): Box = fromCenter(center.x, center.y, size.w, size.h)

  def fromCenter(cx: Double, cy: Double, w: Double, h: Double): Box =
    val x = cx - w / 2
    val y = cy - h / 2
    Box(x, y, w, h)

  val infinite: Box = Box(Double.MinValue, Double.MinValue, Double.MaxValue, Double.MaxValue)
