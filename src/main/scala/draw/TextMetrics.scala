package draw

import geom.Dim

trait TextMetrics:
  def measure(typeface: Typeface, text: String): Dim
