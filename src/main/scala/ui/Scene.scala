package ui

case class SigVal[T](value: T, codec: SceneCodec[T], update: Option[SceneState[T]] = None)

case class Scene(
                  data: Map[SignalSetter[?], SigVal[?]],
                  signalsDeps: Map[SignalSetter[?], Set[WidgetBase]], //List of widgets triggered by each signal
                  widgetDeps: Map[WidgetBase, Set[SignalSetter[?]]], //List of signals that each widget depends on
                  widgetSignals: Map[WidgetBase, Set[SignalSetter[?]]], //List of known signals for each widget
                  dirtyWidgets: Set[WidgetBase] = Set.empty,
                  recording: Option[WidgetBase] = None,
                  suspended: Set[SignalSetter[?]] = Set.empty,
):
  def getData[T](signal: SignalSetter[T]): T = data(signal).value.asInstanceOf[T]

  def getDataOpt[T](signal: SignalSetter[T]): Option[T] = data.get(signal).map(_.value.asInstanceOf[T])

  protected def removeWidget(w: WidgetBase): Scene =
    //remove signals owned by widget and trigger its dependencies
    //remove widget from signal dependencies
    var dw = dirtyWidgets
    var wd = widgetDeps
    var sd = signalsDeps
    var ws = widgetSignals
    var d = data

    ws.getOrElse(w, Set.empty).foreach: s =>
      d -= s
      val deps = sd.getOrElse(s, Set.empty)
      dw ++= deps
      sd -= s

    wd.getOrElse(w, Set.empty).foreach: s =>
      sd = sd.updatedWith(s)(_.map(_ - w).filter(_.nonEmpty))

    ws = ws - w
    wd = wd - w
    dw -= w
    copy(data = d, signalsDeps = sd, widgetDeps = wd, widgetSignals = ws, dirtyWidgets = dw)

  /**
   * Inform the scene that particular widget depends on particular signal. I.e. if
   * the signal changes in the future, the widget should be updated.
    * @param widget
   * @param signal
   */
  protected def addSignalDep(widget: WidgetBase, signal: SignalSetter[?]): Scene =
    copy(
      signalsDeps = signalsDeps.updatedWith(signal)(_.orElse(Some(Set.empty[WidgetBase])).map(_ + widget)),
      widgetDeps = widgetDeps.updatedWith(widget)(_.orElse(Some(Set.empty[SignalSetter[?]])).map(_ + signal)),
    )

  def getAndRegister[T](signal: SignalSetter[T]): Result[T] =
    val ret = getData(signal)
    if recording.isDefined then Trampoline.result(addSignalDep(recording.get, signal), ret)
    else Trampoline.result(this, ret)

  def getAndRegisterOpt[T](signal: SignalSetter[T]): Result[Option[T]] =
    val ret = getDataOpt(signal)
    if recording.isDefined then Trampoline.result(addSignalDep(recording.get, signal), ret)
    else Trampoline.result(this, ret)

  def setData[T, U <: T](signal: SignalSetter[T], value: U, codec: SceneCodec[U]): Scene =
    val d = data + (signal -> SigVal(value, codec))
    var sd = signalsDeps
    var wd = widgetDeps
    val ws = widgetSignals.updatedWith(signal.owner)(_.orElse(Some(Set.empty)).map(_ + signal))
    val dw = if suspended.contains(signal) then dirtyWidgets else dirtyWidgets ++ sd.getOrElse(signal, Set.empty)
    if recording.isDefined then
      val w = recording.get
      sd = sd.updatedWith(signal)(_.orElse(Some(Set.empty)).map(_ + w))
      wd = wd.updatedWith(w)(_.orElse(Some(Set.empty)).map(_ + signal))
    copy(data = d, dirtyWidgets = dw, signalsDeps = sd, widgetDeps = wd, widgetSignals = ws)

  def recordDependencies(widget: WidgetBase): Scene =
    if recording.isDefined then throw Exception("Cannot record start recording when already started")
    val sigs = widgetDeps.getOrElse(widget, Set.empty)
    val wd = widgetDeps - widget
    var sd = signalsDeps
    sigs.foreach: s =>
      sd = sd.updatedWith(s)(_.map(_ - widget).filter(_.nonEmpty))
    copy(recording = Some(widget), widgetDeps = wd, signalsDeps = sd)

  def stopRecording(): Scene =
    if recording.isEmpty then throw Exception("Cannot stop recording when not recording")
    copy(recording = None)

object Scene:

  def getData[T](signal: SignalSetter[T]): SceneState[T] = State(s => s.getAndRegister(signal))
  def getDataOpt[T](signal: SignalSetter[T]): SceneState[Option[T]] = State(s => s.getAndRegisterOpt(signal))
  def setData[T, U <: T](signal: SignalSetter[T], value: U, codec: SceneCodec[U]): SceneState[T] = SceneState(s => Trampoline.result(s.setData(signal, value, codec), value))
  /**
   * Temporarily prevents signal to report changes to its dependencies, run provided state changes and restores
   * signal ability to notify its dependencies. When signal changes during state processing, it will trigger its dependencies.
   * @param signal then signal to suspend
   * @return current value of the signal
   */
  def suspendSignal[A](signal: SignalSetter[A])(state: SceneState[Any]): SceneState[Unit] =
    for
      prev <- SceneState.derive(s => s.copy(suspended = s.suspended + signal), s => s.getData(signal))
      _ <- state
      _ <- SceneState.derive: s =>
        val s1 = s.copy(suspended = s.suspended - signal)
        val sv = s.data(signal).asInstanceOf[SigVal[A]]
        if sv.value != prev then
          s1.setData(signal, prev, SceneCodec.transient).setData(signal, sv.value, sv.codec)
        else s1
    yield ()

  def recordDependencies(widget: WidgetBase)(state: SceneState[Any]): SceneState[Unit] =
    for
      _ <- SceneState.derive(s => s.recordDependencies(widget))
      _ <- state
      _ <- SceneState.derive(s => s.stopRecording())
    yield ()