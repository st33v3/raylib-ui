import geom.{Dim, Segment, Spline, Whit}
import raylib.Point

import scala.collection.mutable.ListBuffer
import scala.xml.XML

object SVG:
  def load(file: String): (Spline, Seq[Whit]) =
    val data = XML.loadFile(file)
    import draw.PathBuilder.*
    val res = spline(Whit(0,0)):

      (data \\ "path").filter(_.attribute("id").exists(_.text == "text")).foreach: path =>
        val d = path.attribute("d").map(_.text).get
        var l = 0
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

          def readArgs() =
            val whit = Whit(args(0), args(1))
            args = args.drop(2)
            if cmd.isUpper then whit else whit + offset

          cmd.toUpper match
            case 'M' => while args.nonEmpty do moveTo(readArgs())
            case 'L' => while args.nonEmpty do lineTo(readArgs())
            case 'Q' => while args.nonEmpty do quadTo(readArgs(), readArgs())
            case 'C' => while args.nonEmpty do cubicTo(readArgs(), readArgs(), readArgs())
            case 'Z' =>
            case _ => println(s"Unknown command $cmd")

    val circles = (data \\ "circle").map: circ =>
      Whit(circ.attribute("cx").map(_.text.toDouble).get, circ.attribute("cy").map(_.text.toDouble).get)

    (res, circles)
