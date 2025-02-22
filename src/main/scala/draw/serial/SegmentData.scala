package draw.serial

import geom.{Segment, Whit}

enum SegmentType:
  case move, line, quad, cubic

case class SegmentData(segment: SegmentType, points: List[(Double, Double)])

object SegmentData:
  def convert(segment: Segment): SegmentData =
    extension (w: Whit) def t: (Double, Double) = (w.x, w.y)
    segment match
      case Segment.Line(p1, p2) => SegmentData(SegmentType.line, List(p1.t, p2.t))
      case Segment.Quad(p1, p2, p3) => SegmentData(SegmentType.quad, List(p1.t, p2.t, p3.t))
      case Segment.Cubic(p1, p2, p3, p4) => SegmentData(SegmentType.cubic, List(p1.t, p2.t, p3.t, p4.t))
      case _ => throw IllegalArgumentException("Unknown segment type")