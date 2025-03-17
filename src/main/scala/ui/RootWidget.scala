package ui

import draw.{Builder, Drawable, Layer, LayerBuilder, Pigment, Style, Uniform}
import geom.Box

class RootWidget extends WidgetBase(WidgetId.root):

  private val factoriesSetter = new SignalSetter[Map[String, WidgetFactory[?]]](this, "factories")
  val factories: Signal[Map[String, WidgetFactory[?]]] = factoriesSetter.signal

  override def parent: WidgetBase = this

  override def draw()(using Scene, LayerBuilder): Unit =
    import Builder.*
    layer(Layer.background):
      style(s => s.copy(brush = Uniform(Pigment.Black))):
        rectangle("root", Box.infinite)

  override def updateChildren(): SceneState[Unit] =
    for
      fs <- this.factories.get
      _ <- State.fold(fs.toList, ())((_, e) => createChild(e._1, e._2).asUnit)
    yield ()

  override protected def init(): SceneState[Unit] = super.init() zipUnit factoriesSetter.set(Map.empty)

  override def toString: String = "RootWidget"

  def addWidget(name: String, widget: WidgetFactory[?]): SceneState[Unit] =
    factoriesSetter.update(_ + (name -> widget)).asUnit

  def removeWidget(name: String): SceneState[Unit] =
    factoriesSetter.update(_ - name).asUnit