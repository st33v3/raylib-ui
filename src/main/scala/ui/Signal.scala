package ui

trait Signal[+T]:
  def owner: Widget
  def name: String
  def apply()(using s: Scene): T
  def get: State[T]
  def getOpt: State[Option[T]]

object NoSignal extends Signal[Nothing]:
  def owner: Widget = throw new NoSuchElementException("NoSignal.owner")
  def name: String = "NoSignal"
  override def apply()(using s: Scene): Nothing = throw new NoSuchElementException("NoSignal.apply")
  override def getOpt: State[None.type] = State.pure(None)
  override def get: State[Nothing] = throw new NoSuchElementException("NoSignal.get")

trait SignalRef[T] extends Signal[Signal[T]]:
  def set(value: Signal[T]): Unit


private class SignalImpl[T](setter: SignalSetter[T]) extends Signal[T]:
  def apply()(using s: Scene): T = s.getData(setter)
  def owner: Widget = setter.owner
  def name: String = setter.name
  def get: State[T] = Scene.getData(setter)
  def getOpt: State[Option[T]] = Scene.getDataOpt(setter)

class SignalSetter[T](val owner: Widget, val name: String):

  val signal: Signal[T] = new SignalImpl[T](this)

  def set[U <: T](value: U)(using codec: SceneCodec[U]): State[T] = Scene.setData(this, value, codec)

  def getChanged: State[Option[T]] = ??? //Scene.getDataChanged(this)

  /**
   * Records Signals used in `fn` and updates signal dependencies
   * Also trigger dependencies of the signal if resulting value changes
   *
   * @param fn
   * @return
   */
  def update[U <: T](fn: T => U)(using codec: SceneCodec[U]): State[T] =
    for
      prev <- signal.get
      next = fn(prev)
      x <- set(next)
    yield x
