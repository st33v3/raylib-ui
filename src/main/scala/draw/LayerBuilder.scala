package draw

import scala.collection.mutable.ListBuffer

class LayerBuilder:
  private val buffer: ListBuffer[Layer] = ListBuffer.empty
  def add(prio: Int, drawable: Drawable): Unit = buffer += Layer(prio, drawable)
  def result(): Seq[Layer] = buffer.result()


