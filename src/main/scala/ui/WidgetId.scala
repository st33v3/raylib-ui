package ui

opaque type WidgetId = String

object WidgetId:
  val root: WidgetId = new WidgetId
  extension (id: WidgetId)
    def append(name: String): WidgetId = if (id.isEmpty) name else s"$id.$name"
