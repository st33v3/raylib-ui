package draw

import geom.{Box, Matrix, Spline}

case class Path(id: String, spline: Spline, style: Style) extends SimpleDrawable:
  def move(x: Double, y: Double): Path = copy(spline = spline.transform(Matrix.move(x, y)))
  override def bounds: Box = spline.bounds


