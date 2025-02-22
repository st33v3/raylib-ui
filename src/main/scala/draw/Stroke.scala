package draw

import zio.json.jsonDiscriminator

@jsonDiscriminator("type")
sealed trait Stroke:
  def lineWidth: Double

case class NoStroke() extends Stroke:
  def lineWidth: Double = 0.0

case class Solid(width: Double, color: Pigment) extends Stroke:
  def lineWidth: Double = width

object Stroke:
  val default: Stroke = Solid(1.0, Pigment.Black)
