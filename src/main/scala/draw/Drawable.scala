package draw

import draw.Style
import geom.{Box, Dim}

import scala.collection.mutable.ListBuffer

trait Drawable:
  def move(dim: Dim): Drawable = move(dim.w, dim.h)
  def move(x: Double, y: Double): Drawable
  def bounds: Box
  private[draw] def collect(buffer: ListBuffer[SimpleDrawable], offsetX: Double, offsetY: Double): Unit
  def collect(): Seq[SimpleDrawable] =
    val buffer = ListBuffer.empty[SimpleDrawable]
    collect(buffer, 0.0, 0.0)
    buffer.toSeq

trait SimpleDrawable extends Drawable:
  def move(x: Double, y: Double): SimpleDrawable
  override protected def collect(buffer: ListBuffer[SimpleDrawable], offsetX: Double, offsetY: Double): Unit =
    buffer += this.move(offsetX, offsetY)