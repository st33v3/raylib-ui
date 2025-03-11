package ui.widgets

import draw.{Drawable, Path, Rectangle, Style}
import geom.{Box, Segment, Spline}
import ui.{Scene, Widget, WidgetBase, WidgetId}

class BoxWidget(id: WidgetId, parent: Widget) extends WidgetBase(id, parent) with Widget2D:
  override def draw()(using Scene): Drawable =
    val box = this.bounds()
    Rectangle(box, Style.default)
  override def toString: String = s"BoxWidget($id)"
