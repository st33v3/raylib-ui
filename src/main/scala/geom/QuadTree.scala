package geom

import raylib.{Point, Rect, Size}

import scala.collection.mutable.ListBuffer


trait Element:
  def overlaps(r: Rect): Boolean
  def covers(r: Rect): Boolean

sealed trait QuadTree:
  val rect: Rect
  final def find(p: Point): Seq[Element] =
    val bld = ListBuffer[Element]()
    findInternal(p, bld)
    bld.result()
  private[geom] def findInternal(p: Point, bld: ListBuffer[Element]): Unit
  def insert(e: Element): QuadTree

private class Leaf(val rect: Rect, val elements: Seq[Element]) extends QuadTree:
  private[geom] def findInternal(p: Point, bld: ListBuffer[Element]): Unit =
    if rect.contains(p) then bld.addAll(elements)

  private def split(): QuadTree =
    if elements.length < 16 then this
    else
      val (cover, partial) = elements.partition(e => e.covers(rect))
      if (partial.isEmpty) this
      else
        val half = Size(rect.size.w / 2, rect.size.h / 2)
        val x = rect.x
        val y = rect.y

        def mkQuad(rect: Rect): QuadTree =
          val els = partial.filter(e => e.overlaps(rect))
          Leaf(rect, els).split()

        val subs = Array(
          mkQuad(Rect(Point(x, y), half)),
          mkQuad(Rect(Point(x + half.w, y), half)),
          mkQuad(Rect(Point(x, y + half.h), half)),
          mkQuad(Rect(Point(x + half.w, y + half.h), half))
        )

        Node(rect, subs, cover)

  override def insert(e: Element): QuadTree =
    Leaf(rect, elements :+ e).split()

private class Node(val rect: Rect, val subs: Array[QuadTree], val elements: Seq[Element]) extends QuadTree:
  private[geom] def findInternal(p: Point, bld: ListBuffer[Element]): Unit =
    bld.addAll(elements)
    for (qt <- subs) do qt.findInternal(p, bld)

  def insert(e: Element): QuadTree =
    if (e.covers(rect)) Node(rect, subs, elements :+ e)
    else
      val nsubs = subs.map(qt => if e.overlaps(qt.rect) then qt.insert(e) else qt)
      Node(rect, nsubs, elements)

object QuadTree:
  def apply(rect: Rect): QuadTree = Leaf(rect, Seq())