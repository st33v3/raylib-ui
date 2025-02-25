package raylib

opaque type MouseCursor = Int

object MouseCursor:
  val MOUSE_CURSOR_DEFAULT: MouseCursor = 0 // Default pointer shape
  val MOUSE_CURSOR_ARROW: MouseCursor = 1 // Arrow shape
  val MOUSE_CURSOR_IBEAM: MouseCursor = 2 // Text writing cursor shape
  val MOUSE_CURSOR_CROSSHAIR: MouseCursor = 3 // Cross shape
  val MOUSE_CURSOR_POINTING_HAND: MouseCursor = 4 // Pointing hand cursor
  val MOUSE_CURSOR_RESIZE_EW: MouseCursor = 5 // Horizontal resize/move arrow shape
  val MOUSE_CURSOR_RESIZE_NS: MouseCursor = 6 // Vertical resize/move arrow shape
  val MOUSE_CURSOR_RESIZE_NWSE: MouseCursor = 7 // Top-left to bottom-right diagonal resize/move arrow shape
  val MOUSE_CURSOR_RESIZE_NESW: MouseCursor = 8 // The top-right to bottom-left diagonal resize/move arrow shape
  val MOUSE_CURSOR_RESIZE_ALL: MouseCursor = 9 // The omnidirectional resize/move cursor shape
  val MOUSE_CURSOR_NOT_ALLOWED: MouseCursor = 10

