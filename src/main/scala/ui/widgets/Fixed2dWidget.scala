package ui.widgets

import geom.{Dim, Whit}
import ui.{Scene, State, Widget, WidgetDataKey}


class Data2d(val pos: Whit, val size: Dim)

object Data2dKey extends WidgetDataKey:
  type Data = Data2d


trait Widget2d:
  this: Widget =>
  def size = Scene.getData(id, Data2dKey).map(_.size)
  def pos = Scene.getData(id, Data2dKey).map(_.pos)


