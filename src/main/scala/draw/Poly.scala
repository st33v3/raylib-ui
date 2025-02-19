package draw

import geom.{Dim, Whit}

case class Poly(points: Seq[Whit], style: Style) extends SimpleDrawable:
  def move(x: Double, y: Double): Poly = copy(points = points.map(_ + Dim(x, y)))

