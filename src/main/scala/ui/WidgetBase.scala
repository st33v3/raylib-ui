package ui

import draw.Drawable

class WidgetBase(id: WidgetId, val parent: Widget) extends Widget(id):
  override def draw()(using Scene): Drawable = ???