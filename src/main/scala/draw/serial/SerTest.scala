package draw.serial

import draw.{Builder, PathBuilder, Style, TextMetrics, Typeface}
import geom.{Box, Dim}
import zio.json.*
import JsonEncoders.given
import scala.sys.process.*

object SerTest:
  def main(args: Array[String]): Unit =
    object fake extends TextMetrics:
      def measure(typeface: Typeface, text: String): Dim = Dim(10, 10)

    import Builder.*
    import PathBuilder.*
    val d = Builder.drawable("", Style.default, Typeface.default, fake):
      rectangle(Box(10.0, 10.0, 100.0, 100.0))

    println("Hello, world!")
    val b = Batch.fromDrawings(d.collect())

    val process = Process("npm.cmd", Seq("run", "svg"))
    val is = new java.io.ByteArrayInputStream(b.toJson.getBytes)
    val output = process #<  is
    //println(process.!)
    println(output.!!)