package ui.widgets

import geom.Box
import ui.{Signal, SignalSetter, WidgetBase}



trait Widget2D:
  this: WidgetBase =>
  private val boundsSetter = new SignalSetter[Box](this, "bounds") 
  val bounds: Signal[Box] = boundsSetter.signal


