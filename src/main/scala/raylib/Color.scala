package raylib

import draw.Pigment
import raylib.Raylib

import java.nio.ByteOrder

opaque type Color = Int
object Color:
  assert(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN, "This code is only designed to work on little-endian systems")

  def apply(r: Int, g: Int, b: Int, a: Int = 255): Color =
    ((a & 255) << 24) | ((b & 255) << 16) | ((g & 255) << 8) | (r & 255)

  private def mkByte(double: Double): Int = (double * 255).toInt

  def fromPigment(pigment: Pigment): Color = Color(mkByte(pigment.r), mkByte(pigment.g), mkByte(pigment.b), mkByte(pigment.a))

  val LIGHTGRAY: Color = Color(200, 200, 200)  // Light Gray
  val GRAY: Color = Color(130, 130, 130)  // Gray
  val DARKGRAY: Color = Color(80, 80, 80)     // Dark Gray
  val YELLOW: Color = Color(253, 249, 0)    // Yellow
  val GOLD: Color = Color(255, 203, 0)    // Gold
  val ORANGE: Color = Color(255, 161, 0)    // Orange
  val PINK: Color = Color(255, 109, 194)  // Pink
  val RED: Color = Color(230, 41, 55)    // Red
  val MAROON: Color = Color(190, 33, 55)    // Maroon
  val GREEN: Color = Color(0, 228, 48)     // Green
  val LIME: Color = Color(0, 158, 47)     // Lime
  val DARKGREEN: Color = Color(0, 117, 44)     // Dark Green
  val SKYBLUE: Color = Color(102, 191, 255)  // Sky Blue
  val BLUE: Color = Color(0, 121, 241)    // Blue
  val DARKBLUE: Color = Color(0, 82, 172)     // Dark Blue
  val PURPLE: Color = Color(200, 122, 255)  // Purple
  val VIOLET: Color = Color(135, 60, 190)   // Violet
  val DARKPURPLE: Color = Color(112, 31, 126)   // Dark Purple
  val BEIGE: Color = Color(211, 176, 131)  // Beige
  val BROWN: Color = Color(127, 106, 79)   // Brown
  val DARKBROWN: Color = Color(76, 63, 47)     // Dark Brown

  val WHITE: Color = Color(255, 255, 255)  // White
  val BLACK: Color = Color(0, 0, 0)        // Black
  val BLANK: Color = Color(0, 0, 0, 0)          // Blank (Transparent)
  val MAGENTA: Color = Color(255, 0, 255)    // Magenta
  val RAYWHITE: Color = Color(245, 245, 245)  // My own White (raylib logo)

  extension (c: Color)
    def b: Int = (c >> 16) & 255
    def g: Int = (c >> 8) & 255
    def r: Int = c & 255
    def a: Int = (c >> 24) & 255
    def withAlpha(a: Int): Color = Color(r, g, b, a)
    def fade(f: Float): Color = Raylib.fade(c, f)