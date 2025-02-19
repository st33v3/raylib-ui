
import draw.{Style, Typeface}
import geom.*
import raylib.Color.*
import raylib.{Color, KeyboardKey, Point, PointBuffer, Raylib, Rect, Size}

val raylibInstance = new Raylib()

import raylibInstance.*

private val starPoints =
  import draw.Builder.*
  val d = drawable(Style.default, Typeface.default):
    star(Whit(200, 200), 60, 30, 7)
  PointBuffer.fromWhits(d.asInstanceOf[draw.Poly].points)

private val starFanPoints =
  val pb = PointBuffer(starPoints.size + 1)
  val center = Point(200, 200)
  pb += center
  // counterclockwise order is required
  starPoints.zipWithIndex.toList.reverse.foreach: (p, _) =>
    pb += p
  println(s"${center.str}, ${pb.size}")
  pb

def mapSegment(s: Segment): (Color, Float) => Unit =
  s match
    case l: Segment.Line =>
      val start = Point.fromWhit(l.start)
      val end = Point.fromWhit(l.end)
      (c: Color, t: Float) =>
        drawSplineSegmentLinear(start, end, t, c)
    case q: Segment.Quad =>
      val start = Point.fromWhit(q.start)
      val cp = Point.fromWhit(q.cp)
      val end = Point.fromWhit(q.end)
      (c: Color, t: Float) =>
        drawSplineSegmentBezierQuadratic(start, cp, end, t, c)
    case c: Segment.Cubic =>
      val start = Point.fromWhit(c.start)
      val cp1 = Point.fromWhit(c.cp1)
      val cp2 = Point.fromWhit(c.cp2)
      val end = Point.fromWhit(c.end)
      (c: Color, t: Float) =>
        drawSplineSegmentBezierCubic(start, cp1, cp2, end, t, c)
@main
def main(): Unit =
  val (spline0, circles0) = SVG.load(".\\vrana.svg")
  val m = Matrix.scale(2)
  val spline = spline0.transform(m)
  val circles = circles0.map(c => m * c)
  println(s"Loaded ${spline.size} spline and ${circles.length} circles")
  println(spline)
  println(spline.segments.map(_.length).sum)
  val segments = spline.segments.map(mapSegment)
  val screen = Rect(0f, 0f, 800f, 480f)
  setTargetFPS(30)
  setConfigFlags(FLAG_MSAA_4X_HINT | FLAG_WINDOW_HIGHDPI | FLAG_VSYNC_HINT)
  initWindow(screen.w.toInt, screen.h.toInt, "raylib [core] example - basic window")
  val catBad = loadTexture(".\\cat-bad.png")
  println(catBad)
  println(catBad.bounds)

  var (x, y) = (0, 0)

  val points = PointBuffer(4)

  var frame = 0

  while !windowShouldClose() do
    if isMouseButtonPressed(MOUSE_BUTTON_LEFT) then
      x = getMouseX
      y = getMouseY

    if isMouseButtonReleased(MOUSE_BUTTON_LEFT) then
      points.truncate(0)

    beginDrawing()
    clearBackground(Color.RAYWHITE)
    drawText("Congrats! You created your first window!", 190, 400, 20, Color.BLACK)
    val m = Whit(getMouseX, getMouseY)
    val minDist = spline.segments.map(_.bounds.sdist(m)).min
    spline.segments.foreach: s =>
      val bbox = s.bounds
      val dist = bbox.sdist(m)
      drawRectangleV(Point.fromWhit(bbox.start), Size.fromDim(bbox.size), if dist =~= minDist then Color.GREEN.fade(0.5f) else Color.YELLOW.fade(0.5f))
    segments.foreach: s =>
      s(Color.BLACK, 2)
    circles.foreach: c =>
      drawCircleV(Point.fromWhit(c), 5, Color.BLUE.fade(0.6f))

    val mp = spline.project(m)
      drawCircleV(Point.fromWhit(mp), 5, Color.RED.fade(0.6f))

    val r = screen.fromCenter(20, 20)
    drawRectangleV(r.start, r.size, Color.GOLD)
    if isMouseButtonDown(MOUSE_BUTTON_LEFT) then
      val x1 = getMouseX
      val y1 = getMouseY
      if isKeyDown(KeyboardKey.LEFT_SHIFT) then
        if points.size == points.limit then points.enlarge(points.limit * 2)
        if (x - x1).abs > 2 || (y - y1).abs > 2 then
          points += Point(x1, y1)
      else
        val x1 = getMouseX min x
        val y1 = getMouseY min y
        val x2 = getMouseX max x
        val y2 = getMouseY max y
        drawRectangle(x1, y1, x2 - x1, y2 - y1, Color.DARKGRAY.fade(0.3f))

    if points.size > 0 then
      if isKeyDown(KeyboardKey.LEFT_CONTROL) then drawSplineLinear(points, 20, Color.SKYBLUE)
      drawSplineLinear(points, 2, Color.DARKBLUE)

    val pb = PointBuffer(4)
    pb += Point(200, 200)
    pb += Point(200, 150)
    pb += Point(150, 200)
    pb += Point(200, 250)
    drawTriangleFan(starFanPoints, Color.YELLOW)
    drawSplineLinear(starPoints, 2, Color.BROWN)
    starFanPoints.foreach: p =>
      drawCircleV(p, 5, Color.RED)
    //drawTriangleStrip(starPoints, Color.PINK)

    //val tr = Rect.fromCenter(Point(600, 300), catBad.size * ((frame % 60).toFloat / 60f))
    val scale = ((frame % 60).toFloat / 60f)
    val tr = Rect(Point(600, 300), catBad.size * scale)
    //val tr = Rect.fromCenter(Point(600, 300), catBad.size)
    val angle = true match
      case _ if isKeyDown(KeyboardKey.RIGHT) => 90
      case _ if isKeyDown(KeyboardKey.LEFT) => -90
      case _ if isKeyDown(KeyboardKey.DOWN) => 180
      case _ if isKeyDown(KeyboardKey.UP) => 0
      case _ => frame.toFloat / 0.3f
    drawTexturePro(catBad, catBad.bounds, tr, (tr.size/ 2).asPoint, angle, Color.BEIGE)
    //drawRectangleLinesEx(tr, 2, Color.RED)
    drawFPS(screen.end.x.toInt - 100, screen.end.y.toInt - 20)

    if !isKeyDown(KeyboardKey.SPACE) then frame += 1
    endDrawing()

  closeWindow()
