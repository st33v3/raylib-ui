package ui

import draw.Drawable

abstract class Widget(val id: WidgetId):
  def draw()(using Scene): Drawable

  override def toString: String = s"Widget($id)"
