package draw

import geom.Box


case class Style(stroke: Stroke, brush: Brush, clip: Box)


object Style:
  val default = Style(Stroke.default, Brush.default, Box.infinite)