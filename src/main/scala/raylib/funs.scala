package raylib

val epsilon = 1e-6f

extension (a: Float)
  def =~=(b: Float): Boolean = (a - b).abs < epsilon
