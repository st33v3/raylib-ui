package draw.serial

import draw.{Brush, Stroke}

import scala.collection.mutable

class BatchBuilder:
  private val brushes = mutable.Map.empty[Brush, String]
  private val strokes = mutable.Map.empty[Stroke, String]
  private val drawings = mutable.ListBuffer.empty[DrawingData]

  def brushId(brush: Brush): String =
    brushes.getOrElseUpdate(brush, s"brush${brushes.size}")

  def strokeId(stroke: Stroke): String =
    strokes.getOrElseUpdate(stroke, s"stroke${brushes.size}")

  def add(data: DrawingData): Unit = drawings += data

  def result(): Batch = Batch(strokes.map(t => (t._2, t._1)).toMap, brushes.map(t => (t._2, t._1)).toMap, drawings.result())
