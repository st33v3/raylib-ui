package raylib

opaque type RaylibFlag = Int

object RaylibFlag:
  val FLAG_VSYNC_HINT: RaylibFlag = 0x00000040 // Set to try enabling V-Sync on GPU
  val FLAG_FULLSCREEN_MODE: RaylibFlag = 0x00000002 // Set to run program in fullscreen
  val FLAG_WINDOW_RESIZABLE: RaylibFlag = 0x00000004 // Set to allow resizable window
  val FLAG_WINDOW_UNDECORATED: RaylibFlag = 0x00000008 // Set to disable window decoration (frame and buttons)
  val FLAG_WINDOW_HIDDEN: RaylibFlag = 0x00000080 // Set to hide window
  val FLAG_WINDOW_MINIMIZED: RaylibFlag = 0x00000200 // Set to minimize window (iconify)
  val FLAG_WINDOW_MAXIMIZED: RaylibFlag = 0x00000400 // Set to maximize window (expanded to monitor)
  val FLAG_WINDOW_UNFOCUSED: RaylibFlag = 0x00000800 // Set to window non focused
  val FLAG_WINDOW_TOPMOST: RaylibFlag = 0x00001000 // Set to window always on top
  val FLAG_WINDOW_ALWAYS_RUN: RaylibFlag = 0x00000100 // Set to allow windows running while minimized
  val FLAG_WINDOW_TRANSPARENT: RaylibFlag = 0x00000010 // Set to allow transparent framebuffer
  val FLAG_WINDOW_HIGHDPI: RaylibFlag = 0x00002000 // Set to support HighDPI
  val FLAG_WINDOW_MOUSE_PASSTHROUGH: RaylibFlag = 0x00004000 // Set to support mouse passthrough, only supported when FLAG_WINDOW_UNDECORATED
  val FLAG_BORDERLESS_WINDOWED_MODE: RaylibFlag = 0x00008000 // Set to run program in borderless windowed mode
  val FLAG_MSAA_4X_HINT: RaylibFlag = 0x00000020 // Set to try enabling MSAA 4X
  val FLAG_INTERLACED_HINT: RaylibFlag = 0x00010000 // Set to try enabling interlaced video format (for V3D)

  extension (flag: RaylibFlag) def |(other: RaylibFlag): RaylibFlag = flag | other
