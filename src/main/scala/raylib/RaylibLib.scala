package raylib

import java.lang.foreign.*
import java.lang.invoke.MethodHandle
import scala.compiletime.erasedValue

private [raylib] object RaylibLib:
  private val arena = Arena.ofAuto()
  private val linker = Linker.nativeLinker()
  private val lookup = SymbolLookup.libraryLookup(".\\raylib\\lib\\raylib.dll", arena)

  private def find(name: String) =
    val ret = lookup.find(name)
    if !ret.isPresent then throw Exception(s"Symbol $name not found")
    ret.get()

  private inline def layout[T <: Tuple]: List[MemoryLayout] = inline erasedValue[T] match
    case _: EmptyTuple => Nil
    case _: (t *: ts) => inline erasedValue[t] match
      case _: Int => ValueLayout.JAVA_INT :: layout[ts]
      case _: Long => ValueLayout.JAVA_LONG :: layout[ts]
      case _: Boolean => ValueLayout.JAVA_BOOLEAN :: layout[ts]
      case _: Float => ValueLayout.JAVA_FLOAT :: layout[ts]
      case _: Double => ValueLayout.JAVA_DOUBLE :: layout[ts]
      case _: MemorySegment => ValueLayout.ADDRESS :: layout[ts]
      case _: String => ValueLayout.ADDRESS :: layout[ts]
      case _: Texture2D => Texture2D.layout :: layout[ts]
      case _: Color => ValueLayout.JAVA_INT :: layout[ts]
      case _: Point => ValueLayout.JAVA_LONG :: layout[ts]
      case _: Size => ValueLayout.JAVA_LONG :: layout[ts]
      case _: Rect => Rect.layout :: layout[ts]

      case _: KeyboardKey => ValueLayout.JAVA_INT :: layout[ts]

  private inline def make[T <: Tuple](name: String): MethodHandle =
    val l = layout[T]
    linker.downcallHandle(find(name), FunctionDescriptor.of(l.head, l.tail *))

  private inline def makeVoid[T <: Tuple](name: String): MethodHandle =
    val l = layout[T]
    linker.downcallHandle(find(name), FunctionDescriptor.ofVoid(l *))

  val SetConfigFlags = makeVoid[Tuple1[Int]]("SetConfigFlags")
  val WindowShouldClose = make[Tuple1[Boolean]]("WindowShouldClose")
  val BeginDrawing = makeVoid[EmptyTuple]("BeginDrawing")
  val EndDrawing = makeVoid[EmptyTuple]("EndDrawing")
  val CloseWindow = makeVoid[EmptyTuple]("CloseWindow")
  val InitWindow = makeVoid[(Int, Int, MemorySegment)]("InitWindow")
  val SetTargetFPS = makeVoid[Tuple1[Int]]("SetTargetFPS")
  val ClearBackground = makeVoid[Tuple1[Int]]("ClearBackground")
  val DrawText = makeVoid[(MemorySegment, Int, Int, Int, Int)]("DrawText")
  val DrawRectangle = makeVoid[(Int, Int, Int, Int, Int)]("DrawRectangle")
  val DrawRectangleV = makeVoid[(Point, Size, Color)]("DrawRectangleV")
  val DrawRectangleLinesEx = makeVoid[(Rect, Float, Color)]("DrawRectangleLinesEx")
  val DrawLineStrip = makeVoid[(MemorySegment, Int, Color)]("DrawLineStrip")
  val DrawCircleV = makeVoid[(Point, Float, Color)]("DrawCircleV")
  val DrawFPS = makeVoid[(Int, Int)]("DrawFPS")

  val DrawSplineLinear = makeVoid[(MemorySegment, Int, Float, Color)]("DrawSplineLinear")

  val DrawSplineSegmentLinear = makeVoid[(Point, Point, Float, Color)]("DrawSplineSegmentLinear")
  val DrawSplineSegmentBezierQuadratic = makeVoid[(Point, Point, Point, Float, Color)]("DrawSplineSegmentBezierQuadratic")
  val DrawSplineSegmentBezierCubic = makeVoid[(Point, Point, Point, Point, Float, Color)]("DrawSplineSegmentBezierCubic")

  val GetSplinePointLinear = make[(Point, Point, Point, Float)]("GetSplinePointLinear")
  val GetSplinePointBezierQuad = make[(Point, Point, Point, Point, Float)]("GetSplinePointBezierQuad")
  val GetSplinePointBezierCubic = make[(Point, Point, Point, Point, Point, Float)]("GetSplinePointBezierCubic")


  val IsMouseButtonPressed = make[(Boolean, Int)]("IsMouseButtonPressed")
  val IsMouseButtonDown = make[(Boolean, Int)]("IsMouseButtonDown")
  val IsMouseButtonReleased = make[(Boolean, Int)]("IsMouseButtonReleased")
  val IsMouseButtonUp = make[(Boolean, Int)]("IsMouseButtonUp")
  val GetMouseX = make[Tuple1[Int]]("GetMouseX")
  val GetMouseY = make[Tuple1[Int]]("GetMouseY")
  val GetMousePosition = make[Tuple1[Point]]("GetMousePosition")
  val GetMouseDelta = make[Tuple1[Point]]("GetMouseDelta")

  val IsKeyPressed = make[(Boolean, KeyboardKey)]("IsKeyDown")
  val IsKeyPressedRepeat = make[(Boolean, KeyboardKey)]("IsKeyDown")
  val IsKeyDown = make[(Boolean, KeyboardKey)]("IsKeyDown")
  val IsKeyReleased = make[(Boolean, KeyboardKey)]("IsKeyReleased")
  val IsKeyUp = make[(Boolean, KeyboardKey)]("IsKeyUp")
  val GetKeyPressed = make[Tuple1[KeyboardKey]]("GetKeyPressed")
  val GetCharPressed = make[Tuple1[Int]]("GetCharPressed")

  val Fade = make[(Color, Color, Float)]("Fade")

  val LoadTexture = make[(Texture2D, String)]("LoadTexture")
  val DrawTextureEx = makeVoid[(Texture2D, Point, Float, Float, Color)]("DrawTextureEx")
  val DrawTexturePro = makeVoid[(Texture2D, Rect, Rect, Point, Float, Color)]("DrawTexturePro")
