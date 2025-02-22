package ui.widgets

import draw.{Drawable, Path, Rectangle, Style}
import geom.{Box, Segment, Spline}
import ui.{Scene, Widget, WidgetId}

class BoxWidget(id: WidgetId) extends Widget(id) with Widget2d:
  def draw()(using Scene): Drawable =
    val p = pos.eval
    val s = size.eval
    Rectangle(Box(p, s), Style.default)
  override def toString: String = s"BoxWidget($id)"
