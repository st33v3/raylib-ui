package draw

import geom.{Box, Dim, Insets, Whit}

case class Poly(id: String, points: Seq[Whit], style: Style) extends SimpleDrawable:
  def move(x: Double, y: Double): Poly = copy(points = points.map(_ + Dim(x, y)))
  def bounds: Box =
    points.tail.foldLeft(Box(points.head, Dim.zero))(_ `include` _).modify(Insets(-style.stroke.lineWidth / 2))

