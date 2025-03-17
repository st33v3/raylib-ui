package draw

import draw.Group.assertUniqueIds
import geom.{Box, Dim}

import scala.collection.mutable.ListBuffer

case class Group(id: String, bounds: Box, children: Seq[Drawable]) extends Drawable:
  assertUniqueIds(children)
  def move(x: Double, y: Double): Group = copy(bounds = bounds.modify(Dim(x, y)))
  private[draw] def collect(buffer: ListBuffer[SimpleDrawable], offsetX: Double, offsetY: Double): Unit =
    children.foreach(_.collect(buffer, offsetX + bounds.x, offsetY + bounds.y))


object Group:
  def assertUniqueIds(children: Seq[Drawable]): Unit =
    val ids = children.map(_.id)
    if ids.distinct.size != ids.size then
      throw IllegalArgumentException(s"Duplicate ids: ${ids.mkString(", ")}")

  def apply(id: String, children: Seq[Drawable]): Group =
    val bounds = children.tail.foldLeft(children.head.bounds)(_ `union` _.bounds)
    new Group(id, bounds, children.map(_.move(-bounds.x, -bounds.y)))
