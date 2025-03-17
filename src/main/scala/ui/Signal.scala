package ui

trait Signal[+T]:
  def owner: WidgetBase
  def name: String
  def apply()(using s: Scene): T
  def get: SceneState[T]
  def getOpt: SceneState[Option[T]]
  def suspend(body: SceneState[Any]): SceneState[Unit]

object NoSignal extends Signal[Nothing]:
  def owner: WidgetBase = throw new NoSuchElementException("NoSignal.owner")
  def name: String = "NoSignal"
  override def apply()(using s: Scene): Nothing = throw new NoSuchElementException("NoSignal.apply")
  override def getOpt: SceneState[None.type] = State.pure(None)
  override def get: SceneState[Nothing] = throw new NoSuchElementException("NoSignal.get")
  override def suspend(body: SceneState[Any]): SceneState[Unit] = body.asUnit

trait SignalRef[T] extends Signal[Signal[T]]:
  def set(value: Signal[T]): Unit


private class SignalImpl[T](setter: SignalSetter[T]) extends Signal[T]:
  def apply()(using s: Scene): T = s.getData(setter)
  def owner: WidgetBase = setter.owner
  def name: String = setter.name
  def get: SceneState[T] = Scene.getData(setter)
  def getOpt: SceneState[Option[T]] = Scene.getDataOpt(setter)
  def suspend(body: SceneState[Any]): SceneState[Unit] = Scene.suspendSignal(setter)(body)

class SignalSetter[T](val owner: WidgetBase, val name: String):

  val signal: Signal[T] = new SignalImpl[T](this)

  def set[U <: T](value: U)(using codec: SceneCodec[U]): SceneState[T] = Scene.setData(this, value, codec)

  def getChanged: SceneState[Option[T]] = ??? //Scene.getDataChanged(this)

  /**
   * Records Signals used in `fn` and updates signal dependencies
   * Also trigger dependencies of the signal if resulting value changes
   *
   * @param fn
   * @return
   */
  def update[U <: T](fn: T => U)(using codec: SceneCodec[U]): SceneState[T] =
    for
      prev <- signal.get
      next = fn(prev)
      x <- set(next)
    yield x
