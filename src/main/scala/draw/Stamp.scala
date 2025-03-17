package draw

import geom.{Box, Dim, Whit}

case class Stamp(id: String, text: String, pos: Whit, bounds: Box, style: Style, typeface: Typeface) extends SimpleDrawable:
  def move(x: Double, y: Double): Stamp = copy(pos = pos + Dim(x, y))
