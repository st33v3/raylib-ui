package ui

import draw.Drawable

abstract class Widget(val id: WidgetId):
  private val childrenSetter = new SignalSetter[Map[String, Widget]](this, "children")
  val children: Signal[Map[String, Widget]] = childrenSetter.signal

  def parent: Widget

  protected def createChild[W <: Widget](name: String, factory: WidgetFactory[W]): State[W] =
    for
      children <- this.children.get
      childOpt = children.get(name).flatMap(c => factory.typecast(c))
      child <- childOpt match
        case Some(c) => State.pure(c)
        case None => factory.create(this, name)
      _ <- childrenSetter.set(children.updated(name, child))
    yield child

  def draw()(using Scene): Drawable

  protected def updateChildren(): State[Unit] = State.pure(())

  def update(): State[Unit] = Scene.suspendSignal(childrenSetter)(updateChildren())

  protected def init(): State[Unit] = State.pure(())

  override def toString: String = s"Widget($id)"


object Widget:
  private[ui] def callInit(w: Widget): State[Unit] = w.init()

