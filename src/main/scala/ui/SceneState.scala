package ui

type SceneState[+A] = State[Scene, A]
type Result[+A] = TrampolineResult[Scene, A]

object SceneState:
  def apply[A](fn: Scene => Trampoline[Scene, A]): SceneState[A] = State(s => fn(s))
  def pure[A](a: A): SceneState[A] = State.pure[Scene, A](a)
  def scene: SceneState[Scene] = State(s => Trampoline.result(s, s))
  def use[A](fn: Scene => A): SceneState[A] = State(s => Trampoline.result(s, fn(s)))
  def derive(fn: Scene => Scene): SceneState[Unit] = State(scene => Trampoline.result(fn(scene), ()))
  def derive[A](fn: Scene => Scene, project: Scene => A): SceneState[A] = State: scene =>
    val s1 = fn(scene)
    Trampoline.result(s1, project(s1))

  def fold[A, B](seq: Seq[A], start: B)(fn: (B, A) => SceneState[B]): SceneState[B] = State.fold(seq, start)(fn)
  
  extension (fa: SceneState[Boolean])
    def when[T](tb: => SceneState[T], eb: => SceneState[T]): SceneState[T] = fa.flatMap(b => if b then tb else eb)
