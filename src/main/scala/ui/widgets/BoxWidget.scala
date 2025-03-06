package ui.widgets

import draw.{Drawable, Path, Rectangle, Style}
import geom.{Box, Segment, Spline}
import ui.{Scene, Widget, WidgetId}

class BoxWidget(id: WidgetId) extends Widget(id) with Widget2D:
  def draw()(using Scene): Drawable =
    val box = this.bounds()
    Rectangle(box, Style.default)
  override def toString: String = s"BoxWidget($id)"
