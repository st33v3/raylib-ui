package draw

case class Pigment(r: Double, g: Double, b: Double, a: Double = 1.0):
  assert(r >= 0 && r <= 1)
  assert(g >= 0 && g <= 1)
  assert(b >= 0 && b <= 1)
  assert(a >= 0 && a <= 1)

  private def limit(x: Double): Double =
    if x < 0 then 0
    else if x > 1 then 1
    else x

  def tint(t: Double): Pigment =
    Pigment(limit(r * t), limit(g * t), limit(b * t), a)

  def tint(t: Pigment): Pigment =
    Pigment(r * t.r, g * t.g, b * t.b, a * t.a)

  def lighter: Pigment = tint(1.1)

object Pigment:
  val Black = Pigment(0, 0, 0)
  val White = Pigment(1, 1, 1)
  val Red = Pigment(1, 0, 0)
  val Green = Pigment(0, 1, 0)
  val Blue = Pigment(0, 0, 1)
  val Yellow = Pigment(1, 1, 0)
  val Cyan = Pigment(0, 1, 1)
  val Magenta = Pigment(1, 0, 1)
  val Transparent = Pigment(0, 0, 0, 0)

  def fromHex(hex: Int): Pigment =
    val r = (hex >> 16) & 0xff
    val g = (hex >> 8) & 0xff
    val b = hex & 0xff
    Pigment(r / 255.0, g / 255.0, b / 255.0)

  def fromHsl(h: Double, s: Double, l: Double): Pigment =
    val c = (1 - Math.abs(2 * l - 1)) * s
    val h1 = h / 60
    val x = c * (1 - Math.abs(h1 % 2 - 1))
    val (r1, g1, b1) =
      if h1 < 1 then (c, x, 0.0)
      else if h1 < 2 then (x, c, 0.0)
      else if h1 < 3 then (0.0, c, x)
      else if h1 < 4 then (0.0, x, c)
      else if h1 < 5 then (x, 0.0, c)
      else (c, 0.0, x)
    val m = l - c / 2
    Pigment(r1 + m, g1 + m, b1 + m)