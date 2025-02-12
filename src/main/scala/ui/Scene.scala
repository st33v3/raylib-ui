package ui

case class CompoundKey(id: WidgetId, key: WidgetDataKey)

class Scene(val widgets: Map[WidgetId, Widget], val data: Map[CompoundKey, Any]):
  def getWidget(id: WidgetId): Widget = widgets(id)
  def getData(id: WidgetId, key: WidgetDataKey): key.Data = data(CompoundKey(id, key)).asInstanceOf[key.Data]
  def setData(id: WidgetId, key: WidgetDataKey)(value: key.Data): Scene = Scene(widgets, data + (CompoundKey(id, key) -> value))

opaque type State[A] = (Scene) => (Scene, A)

object State:
  def apply[A](fn: Scene => (Scene, A)): State[A] = (s: Scene) => fn(s)
  def pure[A](a: A): State[A] = (s: Scene) => (s, a)
  def scene: State[Scene] = (s: Scene) => (s, s)


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
    def eval(using Scene): A =
      val scene = summon[Scene]
      fa(scene)._2

object Scene:
  def getWidget(id: WidgetId): State[Widget] = State(s => (s, s.getWidget(id)))

  def getData(id: WidgetId, key: WidgetDataKey): State[key.Data] = State(s => (s, s.getData(id, key)))
  def setData(id: WidgetId, key: WidgetDataKey)(value: key.Data): State[Unit] = State(s => (s.setData(id, key)(value), ()))
