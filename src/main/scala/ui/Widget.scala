package ui

import draw.Drawable

abstract class Widget(val id: WidgetId):
  val children: Signal[Map[String, Widget]] = new Signal[Map[String, Widget]](this, "children")

  protected def createChild[W <: Widget](id: String, factory: WidgetFactory[W])(init: Widget => State[Unit]): State[W] =
    for
      children <- this.children.get
      childOpt = children.get(id).flatMap(c => factory.typecast(c))
      child <- childOpt match
        case Some(c) => init(c).map(_ => c)
        case None =>
          for
            nc <- factory.create(this, id)
            _ <- init(nc)
          yield nc
    yield child

  def draw()(using Scene): Drawable

  def update(): State[Unit] = State.pure(())

  protected def init(): State[Unit] = State.pure(())

  override def toString: String = s"Widget($id)"


object Widget:
  private[ui] def callInit(w: Widget): State[Unit] = w.init()