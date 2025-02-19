package draw

import geom.{Dim, Segment, Spline, Whit}

import scala.collection.mutable.ListBuffer

type PathBuilderBody = PathBuilder ?=> Unit

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