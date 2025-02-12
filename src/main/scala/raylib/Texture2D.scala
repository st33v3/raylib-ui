package raylib

import java.lang.foreign.{Arena, MemoryLayout, MemorySegment, ValueLayout}

class Texture2D(val id: Int, val width: Int, val height: Int, val mipmaps: Int, val format: Int, private [raylib] val ptr: MemorySegment, private val arena: Arena):
  override def toString: String = s"Texture2D($id, $width, $height, $mipmaps, $format)"
  val size = Size(width, height)
  val bounds = Rect(Point(0, 0), size)

object Texture2D:

  val layout = MemoryLayout.structLayout(
    ValueLayout.JAVA_INT.withName("id"),
    ValueLayout.JAVA_INT.withName("width"),
    ValueLayout.JAVA_INT.withName("height"),
    ValueLayout.JAVA_INT.withName("mipmaps"),
    ValueLayout.JAVA_INT.withName("format"))

  def fromPointer(arena: Arena, ptr: MemorySegment): Texture2D =
    Texture2D(
      ptr.get(ValueLayout.JAVA_INT, 0),
      ptr.get(ValueLayout.JAVA_INT, 4),
      ptr.get(ValueLayout.JAVA_INT, 8),
      ptr.get(ValueLayout.JAVA_INT, 12),
      ptr.get(ValueLayout.JAVA_INT, 16),
      ptr,
      arena
    )