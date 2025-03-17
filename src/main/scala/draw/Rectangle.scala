package draw

import geom.{Box, Dim}

case class Rectangle(id: String, box: Box, style: Style) extends SimpleDrawable:
  def move(x: Double, y: Double): Rectangle = copy(box = box.modify(Dim(x, y)))
  def bounds: Box = box


