package draw

case class Layer(prio: Int, drawable: Drawable)

object Layer:
  val default = 100
  val background = 0