package draw.serial

import geom.Box
import zio.json.jsonDiscriminator

@jsonDiscriminator("type")
sealed trait DrawingData:
  val stroke: String
  val brush: String
  val clip: Option[Box]

case class RectData(stroke: String, brush: String, clip: Option[Box], x: Double, y: Double, width: Double, height: Double) extends DrawingData

case class PolyData(stroke: String, brush: String, clip: Option[Box], points: Seq[(Double, Double)]) extends DrawingData

case class StampData(stroke: String, brush: String, clip: Option[Box], x: Double, y: Double, face: String, text: String) extends DrawingData

case class PathData(stroke: String, brush: String, clip: Option[Box], segments: Seq[SegmentData]) extends DrawingData

