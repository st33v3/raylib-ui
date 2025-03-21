package draw

import geom.{Box, Dim, Matrix, Segment, Spline, Whit}

import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ListBuffer

class Builder[L](
      private var style: Style,
      private var typeface: Typeface,
      val metrics: TextMetrics,
  ):
  private var counter = 0
  private val buffer = ListBuffer.empty[(Drawable, Option[L])]

  def getStyle: Style = style
  def setStyle(style1: Style): Unit = style = style1
  def getTypeface: Typeface = typeface
  def setTypeface(typeface1: Typeface): Unit = typeface = typeface1

  def genId(base: String): String =
    counter += 1
    s"$base$counter"

  def result(): List[(Drawable, Option[L])] = buffer.result()

  def add(drawable: Drawable): Drawable =
    buffer += drawable -> None
    drawable
  def addPair(p: (Drawable, Option[L])): Unit = buffer += p
  def addPairs(ps: Iterable[(Drawable, Option[L])]): Unit = buffer ++= ps
  def attribute(drawable: Drawable, layout: L): Unit =
    val idx = buffer.lastIndexWhere(t => t._1 == drawable)
    if idx >= 0 then buffer.update(idx, (drawable, Some(layout)))
    else throw IllegalArgumentException(s"Drawable not found: $drawable")

  def duplicate[L2](): Builder[L2] =
    new Builder[L2](style, typeface, metrics)

type BuilderBody[L] = Builder[L] ?=> Unit

object Builder:
  val GenerateId = "GenerateId"

  def drawable(id: String, style: Style, typeface: Typeface, metrics: TextMetrics)(build: BuilderBody[Nothing]): Group =
    val bld = new Builder(style, typeface, metrics)
    drawable(id, bld)(build)

  def drawable(id: String, bld: Builder[Nothing])(build: BuilderBody[Nothing]): Group =
    build(using bld)
    Group(id, bld.result().map(_._1))

  def style[L](style1: Style)(build: BuilderBody[L])(using bld: Builder[L]): Unit = style(_ => style1)(build)
  def style[L](styleMod: Style => Style)(build: BuilderBody[L])(using bld: Builder[L]): Unit =
    val bld2 = bld.duplicate[L]()
    bld.setStyle(styleMod(bld.getStyle))
    build(using bld2)
    bld.addPairs(bld2.result())

  def typeface[L](face: Typeface)(build: BuilderBody[L])(using bld: Builder[L]): Unit = typeface(_ => face)(build)
  def typeface[L](faceMod: Typeface => Typeface)(build: BuilderBody[L])(using bld: Builder[L]): Unit =
    val bld2 = bld.duplicate[L]()
    bld.setTypeface(faceMod(bld.getTypeface))
    build(using bld2)
    bld.addPairs(bld2.result())

  def rectangle[L](id: String, box: Box)(using bld: Builder[L]): Drawable =
    bld.add(Rectangle(id, box, bld.getStyle))

  def rectangle[L](box: Box)(using bld: Builder[L]): Drawable = rectangle(bld.genId("box"), box)

  def path[L](id: String, start: Whit, transform: Matrix = Matrix.identity)(build: PathBuilderBody)(using bld: Builder[L]): Drawable =
    val spline0 = PathBuilder.spline(start)(build)
    val spline = if transform.eq(Matrix.identity) then spline0 else spline0.transform(transform)
    bld.add(Path(bld.genId(id), spline, bld.getStyle))

  def path[L](start: Whit)(build: PathBuilderBody)(using bld: Builder[L]): Drawable = path(start, Matrix.identity)(build)
  def path[L](start: Whit, transform: Matrix)(build: PathBuilderBody)(using bld: Builder[L]): Drawable = path(bld.genId("path"), start, transform)(build)

  def poly[L](id: String, points: Seq[Whit])(using bld: Builder[L]): Drawable =
    bld.add(Poly(id, points, bld.getStyle))

  def poly[L](points: Seq[Whit])(using bld: Builder[L]): Drawable = poly(bld.genId("poly"), points)

  def rpoly[L](id: String, center: Whit, radius: Double, vertices: Int, start: Dim = Dim.unitX)(using bld: Builder[L]): Drawable =
    val matrix = Matrix.rotate(start) * Matrix.scale(radius)
    val angle = 2 * Math.PI / vertices
    val points = (0 to vertices).map(i => center + matrix * Dim(Math.cos(i * angle), Math.sin(i * angle)))
    poly(id, points)

  def rpoly[L](center: Whit, radius: Double, vertices: Int)(using bld: Builder[L]): Drawable = rpoly(center, radius, vertices, Dim.unitX)
  def rpoly[L](center: Whit, radius: Double, vertices: Int, start: Dim)(using bld: Builder[L]): Drawable = rpoly(bld.genId("rpoly"), center, radius, vertices, start)

  def star[L](id: String, center: Whit, radius1: Double, radius2: Double, vertices: Int, start: Dim = Dim.unitX)(using bld: Builder[L]): Drawable =
    val matrix1 = Matrix.rotate(start) * Matrix.scale(radius1)
    val matrix2 = Matrix.rotate(start) * Matrix.scale(radius2)
    val angle = Math.PI / vertices
    val points = (0 to 2 * vertices).map(i => center + (if i % 2 == 0 then matrix1 else matrix2) * Dim(Math.cos(i * angle), Math.sin(i * angle)))
    poly(points)

  def star[L](center: Whit, radius1: Double, radius2: Double, vertices: Int)(using bld: Builder[L]): Drawable = star(center, radius1, radius2, vertices, Dim.unitX)
  def star[L](center: Whit, radius1: Double, radius2: Double, vertices: Int, start: Dim)(using bld: Builder[L]): Drawable = star(bld.genId("star"), center, radius1, radius2, vertices, start)

  def text[L](id: String, txt: String, pos: Whit)(using bld: Builder[L]): Drawable =
    val bounds = Box(pos, bld.metrics.measure(bld.getTypeface, txt))
    bld.add(Stamp(id, txt, pos, bounds, bld.getStyle, bld.getTypeface))

  def text[L](txt: String, pos: Whit)(using bld: Builder[L]): Drawable = text(bld.genId("text"), txt, pos)

  private def center(p: (Drawable, Option[Dim])): Whit = p._1.bounds.center + p._2.getOrElse(Dim.zero)

  def stack[L](id: String = GenerateId)(build: BuilderBody[Dim])(using o: Builder[L]): Drawable =
    val bld = o.duplicate[Dim]()
    build(using bld)
    val drawables = bld.result()
    if drawables.isEmpty then throw IllegalArgumentException("Stack must contain at least one drawable")
    val c = center(drawables.head)
    val rest = drawables.tail.map: p =>
      val c2 = center(p)
      p._1.move(c.x - c2.x, c.y - c2.y)
    o.add(Group(o.genId(id), drawables.head._1 +: rest))

  extension (d: Drawable)
    def @@[L](layout: L)(using bld: Builder[L]): Unit =
      bld.attribute(d, layout)

object test:
  def hu(): Unit =
    import Builder.*
    import PathBuilder.*
    object FakeMetrics extends TextMetrics:
      def measure(typeface: Typeface, text: String): Dim = Dim(10, 10)

    val d = drawable("x", Style.default, Typeface.default, FakeMetrics):
      rectangle(Box(Whit(0, 0), Dim(100, 100)))
      path(Whit(0, 0)) {
        moveTo(Whit(10, 10))
        lineTo(Whit(20, 20))
        quadTo(Whit(30, 30), Whit(40, 40))
        cubicTo(Whit(50, 50), Whit(60, 60), Whit(70, 70))
      }
      text("Hello", Whit(80, 80))
      stack():
        rectangle(Box(Whit(0, 0), Dim(10, 10))) @@ Dim(5, 5)
        rectangle(Box(Whit(10, 10), Dim(10, 10)))
