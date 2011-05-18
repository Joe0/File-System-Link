package scala

/**
 * Very simple property replacement that is meant to simulate language level properties.
 *
 * @author Joe Pritzel
 */
class Property[T](var value: T) {
  def :=(v: T) = value = v
  def apply() = value
}