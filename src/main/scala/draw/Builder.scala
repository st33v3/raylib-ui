package draw

import geom.{Box, Dim, Matrix, Segment, Spline, Whit}

import scala.collection.mutable.ListBuffer

class Builder(private var style: Style, private var typeface: Typeface):
  private val buffer = ListBuffer.empty[Drawable]
  private var stack = List.empty[(Style, Typeface)]

  def getStyle: Style = style
  def setStyle(style1: Style): Unit = style = style1
  def getTypeface: Typeface = typeface
  def setTypeface(typeface1: Typeface): Unit = typeface = typeface1

  def result(): List[Drawable] = buffer.result()

  def push(): Unit =
    stack = (style, typeface) :: stack

  def pop(): Unit =
    style = stack.head._1
    typeface = stack.head._2
    stack = stack.tail

  def add(drawable: Drawable): Unit = buffer += drawable

class PathBuilder(private var current: Whit):
  private val segments = ListBuffer.empty[Segment]
  def set(start: Whit): Unit = current = start
  def get: Whit = current
  def add(segment: Segment): Unit = segments += segment
  def result(): Spline = Spline(segments.result())

object PathBuilder:
  def spline(start: Whit)(build: PathBuilderBody): Spline =
    val pbld = new PathBuilder(start)
    build(using pbld)
    pbld.result()

  def moveTo(end: Whit)(using bld: PathBuilder): Unit =
    bld.set(end)

  def lineTo(end: Whit)(using bld: PathBuilder): Unit =
    bld.add(Segment.Line(bld.get, end))
    bld.set(end)

  def quadTo(cp: Whit, end: Whit)(using bld: PathBuilder): Unit =
    bld.add(Segment.Quad(bld.get, cp, end))
    bld.set(end)

  def cubicTo(cp1: Whit, cp2: Whit, end: Whit)(using bld: PathBuilder): Unit =
    bld.add(Segment.Cubic(bld.get, cp1, cp2, end))
    bld.set(end)

  def offset(using bld: PathBuilder): Dim = bld.get.asDim


type PathBuilderBody = PathBuilder ?=> Unit

type BuilderBody = Builder ?=> Unit

object Builder:
  def drawable(style: Style, typeface: Typeface)(build: BuilderBody): Drawable =
    val bld = new Builder(style, typeface)
    build(using bld)
    bld.result().head

  def style(style1: Style)(build: BuilderBody)(using bld: Builder): Unit = style(_ => style1)(build)
  def style(styleMod: Style => Style)(build: BuilderBody)(using bld: Builder): Unit =
    bld.push()
    bld.setStyle(styleMod(bld.getStyle))
    build(using bld)
    bld.pop()

  def typeface(face: Typeface)(build: BuilderBody)(using bld: Builder): Unit = typeface(_ => face)(build)
  def typeface(faceMod: Typeface => Typeface)(build: BuilderBody)(using bld: Builder): Unit =
    bld.push()
    bld.setTypeface(faceMod(bld.getTypeface))
    build(using bld)
    bld.pop()

  def rectangle(box: Box)(using bld: Builder): Unit =
    bld.add(Rectangle(box, bld.getStyle))

  def path(start: Whit, transform: Matrix = Matrix.identity)(build: PathBuilderBody)(using bld: Builder): Unit =
    val spline0 = PathBuilder.spline(start)(build)
    val spline = if transform.eq(Matrix.identity) then spline0 else spline0.transform(transform)
    bld.add(Path(spline, bld.getStyle))

  def poly(points: Seq[Whit])(using bld: Builder): Unit =
    bld.add(Poly(points, bld.getStyle))

  def poly(center: Whit, radius: Double, vertices: Int, start: Dim = Dim.unitX)(using bld: Builder): Unit =
    val matrix = Matrix.rotate(start) * Matrix.scale(radius)
    val angle = 2 * Math.PI / vertices
    val points = (0 until vertices).map(i => center + matrix * Dim(Math.cos(i * angle), Math.sin(i * angle)))
    poly(points)

  def text(text: String, pos: Whit)(using bld: Builder): Unit =
    bld.add(Stamp(text, pos, bld.getStyle, bld.getTypeface))