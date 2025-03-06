package ui.widgets

import geom.Box
import ui.{Signal, Widget}



trait Widget2D:
  this: Widget =>
  val bounds: Signal[Box] = new Signal[Box](this, "bounds")


