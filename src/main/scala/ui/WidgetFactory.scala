package ui

abstract class WidgetFactory[W <: WidgetBase]:
  def typecast(widget: WidgetBase): Option[W]

  /**
   * Creates a new widget with the given parent and id. If the instance needs additional initialization (signals, ...)
   * it should be done in the method.
   * @param parent
   * @param id
   * @return fully initialized widget
   */
  def create(parent: WidgetBase, id: String): SceneState[W] =
    val widget = instantiate(parent, id)
    WidgetBase.callInit(widget).map(_ => widget)

  protected def instantiate(parent: WidgetBase, id: String): W