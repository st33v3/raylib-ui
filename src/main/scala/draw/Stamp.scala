package draw

import geom.{Dim, Whit}

case class Stamp(text: String, pos: Whit, style: Style, typeface: Typeface) extends SimpleDrawable:
  def move(x: Double, y: Double): Stamp = copy(pos = pos + Dim(x, y))
