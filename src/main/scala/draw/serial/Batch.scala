package draw.serial

import draw.{Brush, Path, Poly, Rectangle, SimpleDrawable, Stamp, Stroke, Style}
import geom.Box

case class Batch(strokes: Map[String, Stroke], brushes: Map[String, Brush], drawing: Seq[DrawingData])

object Batch:

  val empty: Batch = Batch(Map.empty, Map.empty, Seq.empty)

  protected def convert(drawing: SimpleDrawable, bld: BatchBuilder): Unit =
    extension (style: Style) def clp: Option[Box] = if style.clip == Box.infinite then None else Some(style.clip)
    val data = drawing match
      case Rectangle(box, style) =>
        RectData(bld.strokeId(style.stroke), bld.brushId(style.brush), style.clp, box.x, box.y, box.w, box.h)
      case Poly(points, style) =>
        PolyData(bld.strokeId(style.stroke), bld.brushId(style.brush), style.clp, points.map(p => (p.x, p.y)))
      case Path(spline, style) =>
        PathData(bld.strokeId(style.stroke), bld.brushId(style.brush), style.clp, spline.segments.map(SegmentData.convert))
    bld.add(data)

  def fromDrawings(drawings: Seq[SimpleDrawable]): Batch =
    val bld = BatchBuilder()
    drawings.foreach(convert(_, bld))
    bld.result()
