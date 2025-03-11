package ui.widgets

import geom.Box
import ui.{Signal, SignalSetter, Widget}



trait Widget2D:
  this: Widget =>
  private val boundsSetter = new SignalSetter[Box](this, "bounds") 
  val bounds: Signal[Box] = boundsSetter.signal


