package draw.serial

enum SegmentType:
  case move, line, quad, cubic

case class SegmentData(segment: SegmentType, points: List[(Double, Double)])
