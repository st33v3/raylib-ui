package ui

import org.scalatest.funsuite.AnyFunSuite

import scala.annotation.tailrec

case class S(i: Int)

type S2[A] = State[S, A]

object S:
  def pure[A](a: A): S2[A] = State.pure[S, A](a)
  def apply[A](fn: S => Trampoline[S, A]): S2[A] = State(fn)
  def state: S2[S] = S(s => Trampoline.result(s, s))
  def get: S2[Int] = S(s => Trampoline.result(s, s.i))
  def set(v: Int): S2[Unit] = S(s => Trampoline.result(s.copy(i = v), ()))
  def update(f: Int => Int): S2[Unit] = S(s => Trampoline.result(s.copy(i = f(s.i)), ()))
  def fold[A, B](seq: Seq[A], start: B)(fn: (B, A) => S2[B]): S2[B] =
    State.fold[S, A, B](seq, start)(fn)

class StateTest extends AnyFunSuite:
  test("flatMap"):
    val fn = for
      a <- S.get
      _ <- S.set(a + 1)
      b <- S.get
    yield b
    assert(fn.run(S(1)) == Trampoline.result(S(2), 2))

  test("flatMap order"):
    val fn = for
      _ <- S.update(_ + 1)
      _ <- S.update(_ * 2)
      _ <- S.update(_ + 1)
      _ <- S.update(_ * 2)
    yield "a"
    assert(fn.run(S(1)) == Trampoline.result(S(10), "a"))

  test("simple fold"):
    val fn = S.fold(Seq(1, 2, 3), 0)((acc, a) => S.pure(acc + a))
    assert(fn.run(S(1)) == Trampoline.result(S(1), 6))

  test("simple fold with many operations"):
    val fn = S.fold(1 to 100000, 0)((acc, a) => S.pure(acc + a))
    assert(fn.run(S(1)) == Trampoline.result(S(1), 705082704))

  test("Many flatMaps"):
    @tailrec
    def add(state: S2[Unit], n: Int): S2[Unit] =
      if n == 0 then state
      else add(state.flatMap(_ => S.update(_ + 1)), n - 1)
    val fn = add(S.pure(()), 100000)
    assert(fn.run(S(0)) == Trampoline.result(S(100000), ()))

  test("combined fold"):
    val fn = S.fold(Seq(1, 2, 3), ()): (_, i) =>
      for
        _ <- S.update(_ + i)
      yield ()
    assert(fn.flatMap(_ => S.get).run(S(1)) == Trampoline.result(S(7), 7))

  test("fold in fold"):
    val fn = S.fold(Seq(1, 2, 3), "")((str, i) => S.fold(i to i + 1, str)((s, j) => S.update(_ + (i * j)).map(_ => s + s"[$i, $j]")))
    assert(fn.run(S(1)) == Trampoline.result(S(35), "[1, 1][1, 2][2, 2][2, 3][3, 3][3, 4]"))