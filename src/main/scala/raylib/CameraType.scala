package raylib

opaque type CameraType = Int

object CameraType:
  extension (ct: CameraType) def asInt: Int = ct
  val CAMERA_PERSPECTIVE: CameraType = 0
  val CAMERA_ORTHOGRAPHIC: CameraType = 1