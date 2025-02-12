package raylib

import draw.Pigment
import raylib.Raylib

import java.nio.ByteOrder

opaque type Color = Int
object Color:
  assert(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)

  def apply(r: Int, g: Int, b: Int, a: Int = 255): Color =
    ((a & 255) << 24) | ((b & 255) << 16) | ((g & 255) << 8) | (r & 255)

  private def mkByte(double: Double): Int = (double * 255).toInt

  def fromPigment(pigment: Pigment): Color = Color(mkByte(pigment.r), mkByte(pigment.g), mkByte(pigment.b), mkByte(pigment.a))

  val LIGHTGRAY  = Color(200, 200, 200, 255)  // Light Gray
  val GRAY       = Color(130, 130, 130, 255)  // Gray
  val DARKGRAY   = Color(80, 80, 80, 255)     // Dark Gray
  val YELLOW     = Color(253, 249, 0, 255)    // Yellow
  val GOLD       = Color(255, 203, 0, 255)    // Gold
  val ORANGE     = Color(255, 161, 0, 255)    // Orange
  val PINK       = Color(255, 109, 194, 255)  // Pink
  val RED        = Color(230, 41, 55, 255)    // Red
  val MAROON     = Color(190, 33, 55, 255)    // Maroon
  val GREEN      = Color(0, 228, 48, 255)     // Green
  val LIME       = Color(0, 158, 47, 255)     // Lime
  val DARKGREEN  = Color(0, 117, 44, 255)     // Dark Green
  val SKYBLUE    = Color(102, 191, 255, 255)  // Sky Blue
  val BLUE       = Color(0, 121, 241, 255)    // Blue
  val DARKBLUE   = Color(0, 82, 172, 255)     // Dark Blue
  val PURPLE     = Color(200, 122, 255, 255)  // Purple
  val VIOLET     = Color(135, 60, 190, 255)   // Violet
  val DARKPURPLE = Color(112, 31, 126, 255)   // Dark Purple
  val BEIGE      = Color(211, 176, 131, 255)  // Beige
  val BROWN      = Color(127, 106, 79, 255)   // Brown
  val DARKBROWN  = Color(76, 63, 47, 255)     // Dark Brown

  val WHITE      = Color(255, 255, 255, 255)  // White
  val BLACK      = Color(0, 0, 0, 255)        // Black
  val BLANK      = Color(0, 0, 0, 0)          // Blank (Transparent)
  val MAGENTA    = Color(255, 0, 255, 255)    // Magenta
  val RAYWHITE   = Color(245, 245, 245, 255)  // My own White (raylib logo)

  extension (c: Color)
    def b: Int = (c >> 16) & 255
    def g: Int = (c >> 8) & 255
    def r: Int = c & 255
    def a: Int = (c >> 24) & 255
    def withAlpha(a: Int): Color = Color(r, g, b, a)
    def fade(f: Float): Color = Raylib.fade(c, f)