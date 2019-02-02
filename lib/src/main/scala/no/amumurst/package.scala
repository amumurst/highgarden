package no

package object amumurst {
  implicit class allOps[T](t: T) {
    def print: T = {
      println(t)
      t
    }
  }

  implicit final class AnyOps[A](self: A) {
    def ===(other: A): Boolean = self == other
  }
}
