package ui

import draw.{Builder, BuilderBody, Drawable, Layer, LayerBuilder, Style, TextMetrics, Typeface}
import geom.Dim

abstract class WidgetBase(val id: WidgetId):
  private val childrenSetter = new SignalSetter[Map[String, WidgetBase]](this, "children")
  val children: Signal[Map[String, WidgetBase]] = childrenSetter.signal

  def parent: WidgetBase

  protected def createChild[W <: WidgetBase](name: String, factory: WidgetFactory[W]): SceneState[W] =
    for
      children <- this.children.get
      childOpt = children.get(name).flatMap(c => factory.typecast(c))
      child <- childOpt match
        case Some(c) => State.pure(c)
        case None => factory.create(this, name)
      _ <- childrenSetter.set(children.updated(name, child))
    yield child

  def draw()(using Scene, LayerBuilder): Unit

  protected def updateChildren(): SceneState[Unit] = State.pure(())

  def update(): SceneState[Unit] = for
    prev <- children.get
    _ <- children.suspend(updateChildren())
    //TODO remove non updated or replaced widgets
  yield ()

  protected def init(): SceneState[Unit] = State.pure(())

  override def toString: String = s"Widget($id)"

  protected def layer(prio: Int = Layer.default)(build: BuilderBody[Nothing])(using lb: LayerBuilder): Unit =
    object fake extends TextMetrics:
      def measure(typeface: Typeface, text: String): Dim = Dim(10, 10)
    val bld = new Builder(Style.default, Typeface.default, fake)
    val group = Builder.drawable(id.toString, bld)(build)
    lb.add(prio, group)

object WidgetBase:
  private[ui] def callInit(w: WidgetBase): SceneState[Unit] = w.init()

