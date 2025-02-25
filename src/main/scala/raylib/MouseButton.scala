package raylib

opaque type MouseButton = Int

object MouseButton:
  val MOUSE_BUTTON_LEFT: MouseButton = 0 // Mouse button left
  val MOUSE_BUTTON_RIGHT: MouseButton = 1 // Mouse button right
  val MOUSE_BUTTON_MIDDLE: MouseButton = 2 // Mouse button middle (pressed wheel)
  val MOUSE_BUTTON_SIDE: MouseButton = 3 // Mouse button side (advanced mouse device)
  val MOUSE_BUTTON_EXTRA: MouseButton = 4 // Mouse button extra (advanced mouse device)
  val MOUSE_BUTTON_FORWARD: MouseButton = 5 // Mouse button forward (advanced mouse device)
  val MOUSE_BUTTON_BACK: MouseButton = 6

