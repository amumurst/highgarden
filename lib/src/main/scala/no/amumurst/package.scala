package no

package object amumurst {
  implicit class allOps[T](t: T) {
    def print: T = {
      println(t)
      t
    }
  }
}
