package ui

opaque type State[+A] = Scene => (Scene, A)

object State:
  def apply[A](fn: Scene => (Scene, A)): State[A] = s => fn(s)
  def pure[A](a: A): State[A] = (s: Scene) => (s, a)
  def scene: State[Scene] = (s: Scene) => (s, s)
  def use[A](fn: Scene => A): State[A] = s => (s, fn(s))
  def derive(fn: Scene => Scene): State[Unit] = scene =>
    val s1 = fn(scene)
    (s1, ())

  def derive[A](fn: Scene => Scene, project: Scene => A): State[A] = scene =>
    val s1 = fn(scene)
    (s1, project(s1))

  def fold[A, B](seq: Iterable[A], start: B)(fn: (A, B) => State[B]): State[B] = scene =>
    var acc = start
    var s = scene
    val it = seq.iterator
    while it.hasNext do
      val a = it.next()
      val (s1, b) = fn(a, acc)(s)
      acc = b
      s = s1
    (s, acc)

  extension [A](fa: State[A])
    def map[B](f: A => B): State[B] = s =>
      val (s1, a) = fa(s)
      (s1, f(a))

    def flatMap[B](f: A => State[B]): State[B] = s =>
      val (s1, a) = fa(s)
      f(a)(s1)

    def mapScene[B](f: Scene ?=> A => B): State[B] = s =>
      val (s1, a) = fa(s)
      given Scene = s1
      (s1, f(a))

    def run(s: Scene): (Scene, A) = fa(s)

    def asUnit: State[Unit] = fa.map(_ => ())
    
    infix def zip[B](fb: State[B]): State[(A, B)] = s =>
      val (s1, a) = fa(s)
      val (s2, b) = fb(s1)
      (s2, (a, b))

    infix def zipLeft(fb: State[Any]): State[A] = s =>
      val (s1, a) = fa(s)
      val (s2, _) = fb(s1)
      (s2, a)
  
    infix def zipRight[B](fb: State[B]): State[B] = s =>
      val (s1, _) = fa(s)
      val (s2, b) = fb(s1)
      (s2, b)
  
    infix def zipUnit(fb: State[Any]): State[Unit] = s =>
      val (s1, _) = fa(s)
      val (s2, _) = fb(s1)
      (s2, ())

  extension (fa: State[Boolean])
    def when[T](tb: => State[T], eb: => State[T]): State[T] = scene =>
      val (s2, b) = fa(scene)
      if b then tb(s2)
      else eb(s2)

