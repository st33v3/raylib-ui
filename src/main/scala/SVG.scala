import geom.{Dim, Segment, Spline, Whit}
import raylib.Point

import scala.collection.mutable.ListBuffer
import scala.xml.XML

object SVG:
  def load(file: String, factor: Float = 1.0f): (Spline, Seq[Whit]) =
    val data = XML.loadFile(file)
    val segments = ListBuffer.empty[Segment]

    (data \\ "path").filter(_.attribute("id").exists(_.text == "text")).foreach: path =>
      val d = path.attribute("d").map(_.text).get
      var l = 0
      var last = Whit(0.0, 0.0)
      while l < d.length do
        val c = d(l)
        if !c.isLetter then throw Exception(s"Expected command, got $c")
        val cmd = c
        l += 2
        var args = Vector.empty[Float]
        while l < d.length && !d(l).isLetter do
          val str = d.substring(l).takeWhile(c => c != ',' && c != ' ' && !c.isLetter)
          args = args :+ str.toFloat
          l+= str.length
          if l < d.length && (d(l) == ',' || d(l) == ' ') then l += 1

        var ll = last
        def readArgs() =
          ll = if cmd.isUpper then Whit(args(0) * factor, args(1) * factor) else last + Dim(args(0) * factor, args(1) * factor)
          args = args.drop(2)
          ll

        def add(s: Segment): Unit =
          segments += s
          last = ll

        cmd.toUpper match
          case 'M' => while args.nonEmpty do readArgs(); last = ll
          case 'L' => while args.nonEmpty do add(Segment.Line(last, readArgs()))
          case 'Q' => while args.nonEmpty do add(Segment.Quad(last, readArgs(), readArgs()))
          case 'C' => while args.nonEmpty do add(Segment.Cubic(last, readArgs(), readArgs(), readArgs()))
          case 'Z' =>
          case _ => println(s"Unknown command $cmd")

    val circles = (data \\ "circle").map: circ =>
      Whit(circ.attribute("cx").map(_.text.toDouble).get * factor, circ.attribute("cy").map(_.text.toDouble).get * factor)

    (Spline(segments.result()), circles)
