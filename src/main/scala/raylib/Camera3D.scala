package raylib

import java.lang.foreign.{Arena, MemoryLayout, MemorySegment, ValueLayout}

/**
 *
 * @param position Camera position
 * @param target Camera target it looks-at
 * @param up Camera up vector (rotation over its axis)
 * @param fovy Camera field-of-view aperture in Y (degrees) in perspective, used as near plane width in orthographic
 * @param projection Camera projection: CAMERA_PERSPECTIVE or CAMERA_ORTHOGRAPHIC
 */
case class Camera3D(position: Point3, target: Point3, up: Point3, fovy: Float, projection: CameraType):
  private val arena = Arena.ofAuto()
  val ptr: MemorySegment = arena.allocate(Camera3D.layout)
  {
    var offset = 0L
    offset = Point3.put(ptr, offset, position)
    offset = Point3.put(ptr, offset, target)
    offset = Point3.put(ptr, offset, up)
    ptr.set(ValueLayout.JAVA_FLOAT, offset, fovy)
    offset += ValueLayout.JAVA_FLOAT.byteSize
    ptr.set(ValueLayout.JAVA_INT, offset, projection.asInt)
  }

  def =~=(that: Camera3D): Boolean = position =~= that.position && target =~= that.target && up =~= that.up && fovy =~= that.fovy && projection == that.projection

object Camera3D:
  val layout: MemoryLayout = MemoryLayout.structLayout(
    Point3.layout,
    Point3.layout,
    Point3.layout,
    ValueLayout.JAVA_FLOAT,
    ValueLayout.JAVA_INT,
  )