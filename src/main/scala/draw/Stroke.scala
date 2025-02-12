package draw

sealed trait Stroke

case class NoStroke() extends Stroke
case class Solid(width: Double, color: Pigment) extends Stroke

object Stroke:
  val default = Solid(1.0, Pigment.Black)
