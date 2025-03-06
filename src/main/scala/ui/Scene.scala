package ui

case class SigVal[T](value: T, codec: SceneCodec[T], update: Option[State[T]] = None)

opaque type State[A] = (Scene) => (Scene, A)

object State:
  def apply[A](fn: Scene => (Scene, A)): State[A] = s => fn(s)
  def pure[A](a: A): State[A] = (s: Scene) => (s, a)
  def scene: State[Scene] = (s: Scene) => (s, s)
  def use[A](fn: Scene => A): State[A] = s => (s, fn(s))
  
  extension [A](fa: State[A])
    def map[B](f: A => B): State[B] = (s: Scene) =>
      val (s1, a) = fa(s)
      (s1, f(a))
    def flatMap[B](f: A => State[B]): State[B] = (s: Scene) =>
      val (s1, a) = fa(s)
      f(a)(s1)
    def mapScene[B](f: Scene ?=> A => B): State[B] = (s: Scene) =>
      val (s1, a) = fa(s)
      given Scene = s1
      (s1, f(a))
    def run(s: Scene): (Scene, A) = fa(s)

  extension (fa: State[Boolean])
    def when[T](tb: => State[T], eb: => State[T]): State[T] = scene =>
      val (s2, b) = fa(scene)
      if b then tb(s2)
      else eb(s2)

case class Scene(
       data: Map[SignalValue[?], SigVal[?]],
       signals: Map[SignalValue[?], Set[Signal[?]]],
       recording: Option[Signal[?]] = None,
):
  def getData[T](signal: SignalValue[T]): T = data(signal).value.asInstanceOf[T]

  protected def addSignalDep(signal: Signal[?], dep: Signal[?]): Scene =
    copy(signals = signals + (signal -> (signals.getOrElse(signal, Set.empty) + dep)))

  def getAndRegister[T](signal: Signal[T]): (Scene, T) =
    val ret = getData(signal)
    if recording.isDefined then addSignalDep(recording.get, signal) -> ret
    else this -> ret

  protected def updateDep[T](signal: Signal[T]): Scene =
    val sv = data(signal).asInstanceOf[SigVal[T]]
    if sv.update.isDefined then
      recordSignal[T](signal, sv.update.get)
    else this

  def setData[T, U <: T](signal: Signal[T], value: U, codec: SceneCodec[U]): Scene =
    if recording.isDefined then throw Exception("Cannot setData while recording another signal")
    //TODO remove this signal from all dependencies and split this method to setData and triggerDeps (used by setData and recordSignal)
    var scene = copy(data = data + (signal -> SigVal(value, codec)))
    val deps = scene.signals.getOrElse(signal, Set.empty).iterator
    while deps.hasNext do
      val dep = deps.next()
      scene = scene.updateDep(dep)
    scene

  def recordSignal[T](signal: Signal[T], update: State[T]): Scene =
    if recording.isDefined then throw Exception("Cannot record another signal while recording a signal")
    var scene = copy(recording = Some(signal), signals = signals - signal)
    val (sc, value) = update.run(scene)
    scene = sc
    val sv = scene.data(signal).asInstanceOf[SigVal[T]]
    scene = scene.copy(data = scene.data + (signal -> sv.copy(update = Some(update), value = value)), recording = None)
    if value != sv.value then
      scene.setData(signal, value, sv.codec)
    else scene

object Scene:

  def getData[T](signal: Signal[T]): State[T] = State(s => s.getAndRegister(signal))
  def setData[T, U <: T](signal: Signal[T], value: U, codec: SceneCodec[U]): State[T] = State(s => (s.setData(signal, value, codec), value))
  def initSignal[T](signal: Signal[T], update: State[T]): State[T] = State(s => (s.recordSignal(signal, update), ())).flatMap(_ => getData(signal))