package draw

case class Typeface(name: String, size: Int, bold: Boolean, italic: Boolean)
  
object Typeface:
  val default: Typeface = Typeface("Arial", 12, false, false)