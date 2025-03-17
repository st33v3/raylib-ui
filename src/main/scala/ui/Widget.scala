package ui

import draw.{Drawable, LayerBuilder}

class Widget(id: WidgetId, val parent: WidgetBase) extends WidgetBase(id):
  override def draw()(using Scene, LayerBuilder): Unit = ()