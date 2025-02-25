package raylib

import geom.Whit

import java.lang.foreign.{Arena, MemoryLayout, MemorySegment, ValueLayout}
import java.util.NoSuchElementException
import scala.annotation.targetName

class PointBuffer(private var _limit: Int) extends Iterable[Point]:
  private var layout = MemoryLayout.sequenceLayout(limit, PointBuffer.layout)
  private var arena = Arena.ofAuto()
  private var ptr = arena.allocate(layout)
  private var _size = 0
  
  def apply(index: Int): Point =
    if (index < 0 || index >= _size) throw IndexOutOfBoundsException(s"Index $index out of bounds")
    Point.fromLong(ptr.get(PointBuffer.layout, index * PointBuffer.layout.byteSize))

  def update(index: Int, value: Point): Unit =
    if (index < 0 || index > _size) throw IndexOutOfBoundsException(s"Index $index out of bounds")
    ptr.set(PointBuffer.layout, index * PointBuffer.layout.byteSize, value.toLong)
    _size = _size max (index + 1)

  @targetName("append")
  def +=(value: Point): Unit = update(_size, value)

  def enlarge(newSize: Int): Unit =
    val newLayout = MemoryLayout.sequenceLayout(newSize, PointBuffer.layout)
    val newArena = Arena.ofAuto()
    val newPtr = newArena.allocate(newLayout)
    MemorySegment.copy(ptr, 0, newPtr, 0, (_size min newSize) * PointBuffer.layout.byteSize)
    ptr = newPtr
    _limit = newSize
    _size = newSize min _size
    layout = newLayout
    arena = newArena

  def truncate(newSize: Int): Unit =
    if newSize < 0 || newSize > _size then throw IndexOutOfBoundsException(s"Size $newSize out of bounds")
    _size = newSize
    
  override def size: Int = _size
  
  def limit: Int = _limit
  
  override def iterator: Iterator[Point] = new Iterator[Point]:
    private var index = 0
    override def hasNext: Boolean = index < _size
    override def next(): Point =
      if !hasNext then throw NoSuchElementException("No more elements")
      val value = apply(index)
      index += 1
      value

  def center: Point =
    var x = 0f
    var y = 0f
    for point <- this do
      x += point.x
      y += point.y
    Point(x / _size, y / _size)

  private[raylib] def pointer: MemorySegment = ptr

object PointBuffer:
  private[raylib] val layout: ValueLayout.OfLong = ValueLayout.JAVA_LONG
  def fromWhits(whits: Seq[Whit]): PointBuffer =
    val buffer = PointBuffer(whits.size)
    whits.foreach(buffer += Point.fromWhit(_))
    buffer