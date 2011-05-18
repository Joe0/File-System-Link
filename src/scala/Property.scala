package scala

class Property[T](var value:T) {
  def :=(v: T) = value = v
  def apply() = value
}