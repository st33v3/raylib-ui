package draw

import geom.Dim

sealed trait Brush

case class NoBrush() extends Brush
case class Uniform(color: Pigment) extends Brush
case class Linear(scale: Scale, direction: Dim) extends Brush
case class Radial(scale: Scale) extends Brush
case class Patch(image: String) extends Brush

object Brush:
  val default: Brush = Uniform(Pigment.Black)
