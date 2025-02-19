package raylib

import java.lang.foreign.{Arena, MemorySegment, SegmentAllocator}
import scala.util.Using.resource

class Raylib:
  private val arena = Arena.ofAuto()
  private val owner = Thread.currentThread()

  private val tmpRect1 = arena.allocate(Rect.layout)
  private val tmpRect2 = arena.allocate(Rect.layout)
  private val tmpRect3 = arena.allocate(Rect.layout)

  def setConfigFlags(flags: Int): Unit = RaylibLib.SetConfigFlags.invokeExact(flags)
  def windowShouldClose(): Boolean = RaylibLib.WindowShouldClose.invokeExact()
  def beginDrawing(): Unit = RaylibLib.BeginDrawing.invokeExact()
  def endDrawing(): Unit = RaylibLib.EndDrawing.invokeExact()
  def closeWindow(): Unit = RaylibLib.CloseWindow.invokeExact()
  def initWindow(width: Int, height: Int, title: String): Unit =
    resource(Arena.ofConfined()): arena =>
      RaylibLib.InitWindow.invokeExact(width, height, arena.allocateFrom(title)): Unit
  def setTargetFPS(fps: Int): Unit = RaylibLib.SetTargetFPS.invokeExact(fps)
  def clearBackground(color: Color): Unit = RaylibLib.ClearBackground.invokeExact(color)
  def drawText(text: String, x: Int, y: Int, size: Int, color: Color): Unit =
    resource(Arena.ofConfined()): arena =>
      RaylibLib.DrawText.invokeExact(arena.allocateFrom(text), x, y, size, color): Unit
  def drawRectangle(x: Int, y: Int, width: Int, height: Int, color: Color): Unit =
    RaylibLib.DrawRectangle.invokeExact(x, y, width, height, color)
  def drawRectangleV(position: Point, size: Size, color: Color): Unit =
    RaylibLib.DrawRectangleV.invokeExact(position, size, color)
  def drawRectangleLinesEx(rectangle: Rect, thick: Float, color: Color): Unit =
    assertThread()
    Rect.put(tmpRect1, rectangle)
    RaylibLib.DrawRectangleLinesEx.invokeExact(tmpRect1, thick, color)

  def drawTriangleFan(points: PointBuffer, color: Color): Unit = RaylibLib.DrawTriangleFan.invokeExact(points.pointer, points.size, color)
  def drawTriangleStrip(points: PointBuffer, color: Color): Unit = RaylibLib.DrawTriangleStrip.invokeExact(points.pointer, points.size, color)

  def drawLineStrip(points: PointBuffer, color: Color): Unit = RaylibLib.DrawLineStrip.invokeExact(points.pointer, points.size, color)
  def drawCircleV(center: Point, radius: Float, color: Color): Unit = RaylibLib.DrawCircleV.invokeExact(center, radius, color)
  def drawFPS(x: Int, y: Int): Unit = RaylibLib.DrawFPS.invokeExact(x, y)
  def drawSplineLinear(points: PointBuffer, thick: Float, color: Color): Unit = RaylibLib.DrawSplineLinear.invokeExact(points.pointer, points.size, thick, color)

  def drawSplineSegmentLinear(a: Point, b: Point, thick: Float, color: Color): Unit = RaylibLib.DrawSplineSegmentLinear.invokeExact(a, b, thick, color)
  def drawSplineSegmentBezierQuadratic(a: Point, c: Point, b: Point, thick: Float, color: Color): Unit = RaylibLib.DrawSplineSegmentBezierQuadratic.invokeExact(a, c, b, thick, color)
  def drawSplineSegmentBezierCubic(a: Point, c1: Point, c2: Point, b: Point, thick: Float, color: Color): Unit = RaylibLib.DrawSplineSegmentBezierCubic.invokeExact(a, c1, c2, b, thick, color)

  def getSplinePointLinear(a: Point, b: Point, t: Float): Point = RaylibLib.GetSplinePointLinear.invokeExact(a, b, t)
  def getSplinePointBezierQuad(a: Point, c: Point, b: Point, t: Float): Point = RaylibLib.GetSplinePointBezierQuad.invokeExact(a, c, b, t)
  def getSplinePointBezierCubic(a: Point, c1: Point, c2: Point, b: Point, t: Float): Point = RaylibLib.GetSplinePointBezierCubic.invokeExact(a, c1, c2, b, t)

  def isMouseButtonPressed(button: Int): Boolean = RaylibLib.IsMouseButtonPressed.invokeExact(button)
  def isMouseButtonDown(button: Int): Boolean = RaylibLib.IsMouseButtonDown.invokeExact(button)
  def isMouseButtonReleased(button: Int): Boolean = RaylibLib.IsMouseButtonReleased.invokeExact(button)
  def isMouseButtonUp(button: Int): Boolean = RaylibLib.IsMouseButtonUp.invokeExact(button)
  def getMouseX(): Int = RaylibLib.GetMouseX.invokeExact()
  def getMouseY(): Int = RaylibLib.GetMouseY.invokeExact()
  def getMousePosition(): Point = RaylibLib.GetMousePosition.invokeExact()
  def getMouseDelta(): Point = RaylibLib.GetMouseDelta.invokeExact()

  def isKeyDown(key: KeyboardKey): Boolean = RaylibLib.IsKeyDown.invokeExact(key)
  def isKeyPressed(key: KeyboardKey): Boolean = RaylibLib.IsKeyPressed.invokeExact(key)
  def isKeyReleased(key: KeyboardKey): Boolean = RaylibLib.IsKeyReleased.invokeExact(key)
  def isKeyUp(key: KeyboardKey): Boolean = RaylibLib.IsKeyUp.invokeExact(key)
  def getKeyPressed(): KeyboardKey = RaylibLib.GetKeyPressed.invokeExact()
  def getCharPressed(): Int = RaylibLib.GetCharPressed.invokeExact()

  val MOUSE_BUTTON_LEFT    = 0       // Mouse button left
  val MOUSE_BUTTON_RIGHT   = 1       // Mouse button right
  val MOUSE_BUTTON_MIDDLE  = 2       // Mouse button middle (pressed wheel)
  val MOUSE_BUTTON_SIDE    = 3       // Mouse button side (advanced mouse device)
  val MOUSE_BUTTON_EXTRA   = 4       // Mouse button extra (advanced mouse device)
  val MOUSE_BUTTON_FORWARD = 5       // Mouse button forward (advanced mouse device)
  val MOUSE_BUTTON_BACK    = 6

  val MOUSE_CURSOR_DEFAULT = 0 // Default pointer shape
  val MOUSE_CURSOR_ARROW = 1 // Arrow shape
  val MOUSE_CURSOR_IBEAM = 2 // Text writing cursor shape
  val MOUSE_CURSOR_CROSSHAIR = 3 // Cross shape
  val MOUSE_CURSOR_POINTING_HAND = 4 // Pointing hand cursor
  val MOUSE_CURSOR_RESIZE_EW = 5 // Horizontal resize/move arrow shape
  val MOUSE_CURSOR_RESIZE_NS = 6 // Vertical resize/move arrow shape
  val MOUSE_CURSOR_RESIZE_NWSE = 7 // Top-left to bottom-right diagonal resize/move arrow shape
  val MOUSE_CURSOR_RESIZE_NESW = 8 // The top-right to bottom-left diagonal resize/move arrow shape
  val MOUSE_CURSOR_RESIZE_ALL = 9 // The omnidirectional resize/move cursor shape
  val MOUSE_CURSOR_NOT_ALLOWED = 10

  val FLAG_VSYNC_HINT         = 0x00000040   // Set to try enabling V-Sync on GPU
  val FLAG_FULLSCREEN_MODE    = 0x00000002   // Set to run program in fullscreen
  val FLAG_WINDOW_RESIZABLE   = 0x00000004   // Set to allow resizable window
  val FLAG_WINDOW_UNDECORATED = 0x00000008   // Set to disable window decoration (frame and buttons)
  val FLAG_WINDOW_HIDDEN      = 0x00000080   // Set to hide window
  val FLAG_WINDOW_MINIMIZED   = 0x00000200   // Set to minimize window (iconify)
  val FLAG_WINDOW_MAXIMIZED   = 0x00000400   // Set to maximize window (expanded to monitor)
  val FLAG_WINDOW_UNFOCUSED   = 0x00000800   // Set to window non focused
  val FLAG_WINDOW_TOPMOST     = 0x00001000   // Set to window always on top
  val FLAG_WINDOW_ALWAYS_RUN  = 0x00000100   // Set to allow windows running while minimized
  val FLAG_WINDOW_TRANSPARENT = 0x00000010   // Set to allow transparent framebuffer
  val FLAG_WINDOW_HIGHDPI     = 0x00002000   // Set to support HighDPI
  val FLAG_WINDOW_MOUSE_PASSTHROUGH = 0x00004000 // Set to support mouse passthrough, only supported when FLAG_WINDOW_UNDECORATED
  val FLAG_BORDERLESS_WINDOWED_MODE = 0x00008000 // Set to run program in borderless windowed mode
  val FLAG_MSAA_4X_HINT       = 0x00000020   // Set to try enabling MSAA 4X
  val FLAG_INTERLACED_HINT    = 0x00010000    // Set to try enabling interlaced video format (for V3D)

  def loadTexture(fileName: String): Texture2D =    // Load texture from file into GPU memory (VRAM)
    resource(Arena.ofConfined()): arena =>
      val fileNamePtr = arena.allocateFrom(fileName)
      val allocator = Arena.ofAuto()
      val texturePtr = RaylibLib.LoadTexture.invokeExact(allocator: SegmentAllocator, fileNamePtr): MemorySegment
      Texture2D.fromPointer(allocator, texturePtr)

  def drawTextureEx(texture: Texture2D, position: Point, rotation: Float, scale: Float, color: Color): Unit =
    RaylibLib.DrawTextureEx.invokeExact(texture.ptr, position, rotation, scale, color)

  def drawTexturePro(texture: Texture2D, source: Rect, dest: Rect, origin: Point, rotation: Float, color: Color): Unit =
    assertThread()
    Rect.put(tmpRect1, source)
    Rect.put(tmpRect2, dest)
    RaylibLib.DrawTexturePro.invokeExact(texture.ptr, tmpRect1, tmpRect2, origin, rotation, color)

  private def assertThread(): Unit =
    if Thread.currentThread() != owner then throw Exception("Raylib must be called from the same thread")

/**
 * Raylib functions that may be shared between threads (not drawing methods).
 * They should be made accessible as extensions of specific Raylib classes such as color, rect, etc.
 */
object Raylib:
  def fade(c: Color, f: Float): Color = RaylibLib.Fade.invokeExact(c, f)
