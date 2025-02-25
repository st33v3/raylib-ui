package raylib

import java.lang.foreign.{Arena, MemorySegment, SegmentAllocator}
import scala.util.Using.resource

class Raylib:
  private val arena = Arena.ofAuto()
  private val owner = Thread.currentThread()

  private val tmpRect1 = arena.allocate(Rect.layout)
  private val tmpRect2 = arena.allocate(Rect.layout)
  private val tmpRect3 = arena.allocate(Rect.layout)

  private val tmpPoint1 = arena.allocate(Point3.layout)
  private val tmpPoint2 = arena.allocate(Point3.layout)
  private val tmpPoint3 = arena.allocate(Point3.layout)

  def setConfigFlags(flags: RaylibFlag): Unit = RaylibLib.SetConfigFlags.invokeExact(flags)
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
    Rect.put(tmpRect1, 0, rectangle)
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

  def isMouseButtonPressed(button: MouseButton): Boolean = RaylibLib.IsMouseButtonPressed.invokeExact(button)
  def isMouseButtonDown(button: MouseButton): Boolean = RaylibLib.IsMouseButtonDown.invokeExact(button)
  def isMouseButtonReleased(button: MouseButton): Boolean = RaylibLib.IsMouseButtonReleased.invokeExact(button)
  def isMouseButtonUp(button: MouseButton): Boolean = RaylibLib.IsMouseButtonUp.invokeExact(button)
  def getMouseX: Int = RaylibLib.GetMouseX.invokeExact()
  def getMouseY: Int = RaylibLib.GetMouseY.invokeExact()
  def getMousePosition: Point = RaylibLib.GetMousePosition.invokeExact()
  def getMouseDelta: Point = RaylibLib.GetMouseDelta.invokeExact()

  def isKeyDown(key: KeyboardKey): Boolean = RaylibLib.IsKeyDown.invokeExact(key)
  def isKeyPressed(key: KeyboardKey): Boolean = RaylibLib.IsKeyPressed.invokeExact(key)
  def isKeyReleased(key: KeyboardKey): Boolean = RaylibLib.IsKeyReleased.invokeExact(key)
  def isKeyUp(key: KeyboardKey): Boolean = RaylibLib.IsKeyUp.invokeExact(key)
  def getKeyPressed: KeyboardKey = RaylibLib.GetKeyPressed.invokeExact()
  def getCharPressed: Int = RaylibLib.GetCharPressed.invokeExact()

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
    Rect.put(tmpRect1, 0, source)
    Rect.put(tmpRect2, 0, dest)
    RaylibLib.DrawTexturePro.invokeExact(texture.ptr, tmpRect1, tmpRect2, origin, rotation, color)

  def beginMode3D(camera: Camera3D): Unit = RaylibLib.BeginMode3D.invokeExact(camera.ptr)
  def endMode3D(): Unit = RaylibLib.EndMode3D.invokeExact()
  def drawCube(position: Point3, width: Float, height: Float, length: Float, color: Color): Unit =
    assertThread()
    Point3.put(tmpPoint1, 0, position)
    RaylibLib.DrawCube.invokeExact(tmpPoint1, width, height, length, color)
  def drawCubeV(position: Point3, size: Point3, color: Color): Unit =
    assertThread()
    Point3.put(tmpPoint1, 0, position)
    Point3.put(tmpPoint2, 0, size)
    RaylibLib.DrawCubeV.invokeExact(tmpPoint1, tmpPoint2, color)
  def drawCubeWires(position: Point3, width: Float, height: Float, length: Float, color: Color): Unit =
    assertThread()
    Point3.put(tmpPoint1, 0, position)
    RaylibLib.DrawCubeWires.invokeExact(tmpPoint1, width, height, length, color)
  def drawCubeWiresV(position: Point3, size: Point3, color: Color): Unit =
    assertThread()
    Point3.put(tmpPoint1, 0, position)
    Point3.put(tmpPoint2, 0, size)
    RaylibLib.DrawCubeWiresV.invokeExact(tmpPoint1, tmpPoint2, color)

  private def assertThread(): Unit =
    if Thread.currentThread() != owner then throw Exception("Raylib must be called from the same thread")

/**
 * Raylib functions that may be shared between threads (not drawing methods).
 * They should be made accessible as extensions of specific Raylib classes such as color, rect, etc.
 */
object Raylib:
  def fade(c: Color, f: Float): Color = RaylibLib.Fade.invokeExact(c, f)
