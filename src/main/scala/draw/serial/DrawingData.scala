package draw.serial

import geom.Box


sealed trait DrawingData:
  val shape: String
  val stroke: String
  val brush: String
  val clip: Option[Box]

case class RectData(shape: "rect", stroke: String, brush: String, clip: Option[Box], x: Double, y: Double, width: Double, height: Double) extends DrawingData

case class PolyData(shape: "poly", stroke: String, brush: String, clip: Option[Box], points: List[(Double, Double)]) extends DrawingData

case class StampData(shape: "stamp", stroke: String, brush: String, clip: Option[Box], x: Double, y: Double, face: String, text: String) extends DrawingData

case class PathData(shape: "path", stroke: String, brush: String, clip: Option[Box], segments: Seq[SegmentData]) extends DrawingData
