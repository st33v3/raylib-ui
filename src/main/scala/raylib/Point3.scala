package raylib

import java.lang.foreign.{MemoryLayout, MemorySegment, ValueLayout}

case class Point3(x: Float, y: Float, z: Float):
  def =~=(that: Point3): Boolean = x =~= that.x && y =~= that.y && z =~= that.z

object Point3:
  val layout = MemoryLayout.structLayout(ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT, ValueLayout.JAVA_FLOAT)

  def put(ptr: MemorySegment, offset0: Long, point: Point3): Long =
    var offset = offset0
    ptr.set(ValueLayout.JAVA_FLOAT, offset, point.x)
    offset += ValueLayout.JAVA_FLOAT.byteSize
    ptr.set(ValueLayout.JAVA_FLOAT, offset, point.y)
    offset += ValueLayout.JAVA_FLOAT.byteSize
    ptr.set(ValueLayout.JAVA_FLOAT, offset, point.z)
    offset + ValueLayout.JAVA_FLOAT.byteSize
