package ui.widgets

import draw.{Drawable, LayerBuilder, Rectangle, Style, Builder}
import ui.{Scene, Widget, WidgetBase, WidgetId}

class BoxWidget(id: WidgetId, parent: WidgetBase) extends Widget(id, parent) with Widget2D:

  override def draw()(using Scene, LayerBuilder): Unit =
    import Builder.*
    layer():
      rectangle(bounds())

  override def toString: String = s"BoxWidget($id)"

