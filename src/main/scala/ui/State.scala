package ui

import scala.annotation.tailrec

// https://medium.com/%40olxc/trampolining-and-stack-safety-in-scala-d8e86474ddfa
// https://days2012.scala-lang.org/sites/days2012/files/bjarnason_trampolines.pdf

opaque type State[S, +A] = S => Trampoline[S, A]

sealed trait Trampoline[S, +A]:
  def next(): Trampoline[S, A]

trait TrampolineResult[S, +A] extends Trampoline[S, A]:
  def value: A
  def state: S

trait TrampolineOps[S, A]:
  private[TrampolineOps] def thenFlatMap[C](fm: A => State[S, C]): Trampoline[S, C]
  private[TrampolineOps] def thenFold[C](seq: Seq[C], fold: (A, C) => State[S, A]): Trampoline[S, A]

object TrampolineOps:
  case class Done[S, A](state: S, value: A) extends Trampoline[S, A] with TrampolineOps[S, A] with TrampolineResult[S, A]:
    def next(): Trampoline[S, A] = this
    private[TrampolineOps] override def thenFlatMap[C](fm: A => State[S, C]): Trampoline[S, C] =
      fm(value).applyState(state)
    private[TrampolineOps] override def thenFold[C](seq: Seq[C], fold: (A, C) => State[S, A]): Trampoline[S, A] =
      State.fold(seq, value)(fold).applyState(state)

  case class FlatMap[S, B, A](state: S, sub: State[S, B], fn: B => State[S, A]) extends Trampoline[S, A] with TrampolineOps[S, A]:

    override def next(): Trampoline[S, A] =
      val s = sub.applyState(state).asInstanceOf[TrampolineOps[S, B]]
      s.thenFlatMap(fn)

    private[TrampolineOps] override def thenFlatMap[C](fm: A => State[S, C]): Trampoline[S, C] =
      FlatMap(state, sub, a => State(s => FlatMap(s, fn(a), fm)))

    private[TrampolineOps] override def thenFold[C](seq: Seq[C], fold: (A, C) => State[S, A]): Trampoline[S, A] =
      thenFlatMap(b => State.fold(seq, b)(fold))

  case class Fold[S, B, A](state: S, seq: Seq[B], acc: A, fn: (A, B) => State[S, A]) extends Trampoline[S, A] with TrampolineOps[S, A]:
    private def step(state: S): Trampoline[S, A] =
      if seq.isEmpty then Done(state, acc)
      else
        val a = seq.head
        fn(acc, a).applyState(state).asInstanceOf[TrampolineOps[S, A]].thenFold(seq.tail, fn)

    override def next(): Trampoline[S, A] = step(state)

    private[TrampolineOps] override def thenFlatMap[C](fm: A => State[S, C]): Trampoline[S, C] =
      FlatMap(state, State(s => this.step(s)), fm)

    private[TrampolineOps] override def thenFold[C](seq: Seq[C], fold: (A, C) => State[S, A]): Trampoline[S, A] =
      thenFlatMap(b => State.fold(seq, b)(fold))

import TrampolineOps.*

object Trampoline:

  def result[S, A](state: S, value: A): TrampolineResult[S, A] = Done(state, value)

  @tailrec
  def run[S, A](t: Trampoline[S, A]): TrampolineResult[S, A] =
    t match
      case d @ Done(_,_) => d
      case _ => run(t.next())

object State:
  def pure[S, A](a: A): State[S, A] = s => Done(s, a)
  def apply[S, A](fa: S => Trampoline[S, A]): State[S, A] = fa
  def fold[S, A, B](seq: Seq[A], init: B)(fn: (B, A) => State[S, B]): State[S, B] =
    state => Fold(state, seq, init, fn)
  def zip[S, A, B, R](s1: State[S, A], s2: State[S, B], sel: (A, B) => R): State[S, R] = for
    a <- s1
    b <- s2
  yield sel(a, b)

  private def selLeft[A, B](a: A, b: B) = a
  private def selRight[A, B](a: A, b: B) = b
  private def selBoth[A, B](a: A, b: B) = (a, b)
  private def selUnit(a: Any, b: Any) = a

  extension [S, A](fa: State[S, A])
    def applyState(s: S): Trampoline[S, A] = fa(s)
    def flatMap[B](f: A => State[S, B]): State[S, B] = state => FlatMap(state, fa, f)
    def map[B](f: A => B): State[S, B] = flatMap(a => State.pure(f(a)))
    def run(state: S): TrampolineResult[S, A] = Trampoline.run(fa(state))
    infix def zip[B](fb: State[S, B]): State[S, (A, B)] = State.zip(fa, fb, selBoth)
    infix def zipLeft[B](fb: State[S, B]): State[S, A] = State.zip(fa, fb, selLeft)
    infix def zipRight[B](fb: State[S, B]): State[S, B] = State.zip(fa, fb, selRight)
    infix def zipUnit[B](fb: State[S, B]): State[S, Unit] = State.zip(fa, fb, selUnit)
    infix def asUnit: State[S, Unit] = fa.map(_ => ())
