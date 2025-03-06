package ui

abstract class WidgetFactory[W <: Widget]:
  def typecast(widget: Widget): Option[W]

  /**
   * Creates a new widget with the given parent and id. If the instance needs additional initialization (signals, ...)
   * it should be done in the method.
   * @param parent
   * @param id
   * @return fully initialized widget
   */
  def create(parent: Widget, id: String): State[W] =
    val widget = instantiate(parent, id)
    Widget.callInit(widget).map(_ => widget)

  protected def instantiate(parent: Widget, id: String): W