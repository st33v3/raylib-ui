package draw.serial

import draw.{Brush, Pigment, Scale, Stroke}
import geom.{Box, Dim, Whit}
import zio.json.{DeriveJsonEncoder, JsonEncoder}

object JsonEncoders:
  given JsonEncoder[Whit] = JsonEncoder[(Double, Double)].contramap(w => (w.x, w.y))
  given JsonEncoder[Dim] = JsonEncoder[(Double, Double)].contramap(d => (d.w, d.h))
  given JsonEncoder[Box] = DeriveJsonEncoder.gen[Box]
  given JsonEncoder[Pigment] = DeriveJsonEncoder.gen[Pigment]
  given JsonEncoder[Scale] = DeriveJsonEncoder.gen[Scale]
  given JsonEncoder[Stroke] = DeriveJsonEncoder.gen[Stroke]
  given JsonEncoder[Brush] = DeriveJsonEncoder.gen[Brush]
  given JsonEncoder[SegmentType] = JsonEncoder[String].contramap(_.toString)
  given JsonEncoder[SegmentData] = DeriveJsonEncoder.gen[SegmentData]
  given JsonEncoder[DrawingData] = DeriveJsonEncoder.gen[DrawingData]
  given JsonEncoder[Batch] = DeriveJsonEncoder.gen[Batch]

