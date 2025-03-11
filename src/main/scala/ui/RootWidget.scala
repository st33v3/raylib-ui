package ui

import draw.Drawable

class RootWidget extends Widget(WidgetId.root):

  private val factoriesSetter = new SignalSetter[Map[String, WidgetFactory[?]]](this, "factories")
  val factories: Signal[Map[String, WidgetFactory[?]]] = factoriesSetter.signal

  override def parent: Widget = this

  override def draw()(using Scene): Drawable = ???

  override def updateChildren(): State[Unit] =
    for
      fs <- this.factories.get
      _ <- State.fold(fs, ())((e, _) => createChild(e._1, e._2).asUnit)
    yield ()
  
  override protected def init(): State[Unit] = super.init() zipUnit factoriesSetter.set(Map.empty)

  override def toString: String = "RootWidget"

  def addWidget(name: String, widget: WidgetFactory[?]): State[Unit] =
    factoriesSetter.update(_ + (name -> widget)).asUnit

  def removeWidget(name: String): State[Unit] =
    factoriesSetter.update(_ - name).asUnit