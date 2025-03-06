package ui

import scala.reflect.ClassTag

class InitProps[+A]:
  def add[T](v: T)(using ClassTag[T]): InitProps[A & T] = ???
  def get[T >: A](using ClassTag[T]): T = ???

object InitProps:
  val empty: InitProps[Any] = ???
  def test: Unit =
    val a = empty.add(1).add("sdf")
    val x = a.get[String]