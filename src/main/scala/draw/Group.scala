package draw

import geom.{Box, Dim}

import scala.collection.mutable.ListBuffer

case class Group(bounds: Box, children: Seq[Drawable]) extends Drawable:
  def move(x: Double, y: Double): Group = copy(bounds = bounds.modify(Dim(x, y)))
  private[draw] def collect(buffer: ListBuffer[SimpleDrawable], offsetX: Double, offsetY: Double): Unit =
    children.foreach(_.collect(buffer, offsetX + bounds.x, offsetY + bounds.y))


object Group:
  def apply(children: Seq[Drawable]): Group =
    val bounds = children.tail.foldLeft(children.head.bounds)(_ `union` _.bounds)
    new Group(bounds, children.map(_.move(-bounds.x, -bounds.y)))
