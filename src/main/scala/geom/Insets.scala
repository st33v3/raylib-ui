package geom

case class Insets(top: Double, right: Double, bottom: Double, left: Double):
  def +(that: Insets): Insets = Insets(top + that.top, right + that.right, bottom + that.bottom, left + that.left)
  def unary_- : Insets = Insets(-top, -right, -bottom, -left)
  def coordinate(dir: Dir): Double =
    dir match
      case Dir.N => top
      case Dir.E => right
      case Dir.S => bottom
      case Dir.W => left
      case _ => throw IllegalArgumentException(s"Not a main direction: $dir")

object Insets:
  def apply(all: Double): Insets = Insets(all, all, all, all)
  def apply(horizontal: Double, vertical: Double): Insets = Insets(vertical, horizontal, vertical, horizontal)
  val zero: Insets = Insets(0, 0, 0, 0)