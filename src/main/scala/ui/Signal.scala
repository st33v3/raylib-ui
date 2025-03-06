package ui

trait SignalValue[+T]:
  def owner: Widget
  def name: String
  def apply()(using s: Scene): T = s.getData(this)

object NoSignal extends SignalValue[Nothing]:
  def owner: Widget = throw new NoSuchElementException("NoSignal.owner")
  def name: String = "NoSignal"
  override def apply()(using s: Scene): Nothing = throw new NoSuchElementException("NoSignal.apply")

trait SignalRef[T] extends SignalValue[SignalValue[T]]:
  def set(value: SignalValue[T]): Unit


class Signal[T](val owner: Widget, val name: String) extends SignalValue[T]:

  def get: State[T] = Scene.getData(this)
  def set[U <: T](value: U)(using codec: SceneCodec[U]): State[T] = Scene.setData(this, value, codec)
  def getOpt: State[Option[T]] = ??? //Scene.getDataOpt(this)
  def getChanged: State[Option[T]] = ??? //Scene.getDataChanged(this)
  /**
   * Records Signals used in `fn` and updates signal dependencies
   * Also trigger dependencies of the signal if resulting value changes
   * @param fn
   * @return
   */
  def update[U <: T](fn: T => U)(using codec: SceneCodec[U]): State[T] =
    for
      prev <- get
      next = fn(prev)
      x <- set(next)
    yield x
