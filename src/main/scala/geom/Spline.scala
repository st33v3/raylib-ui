package geom

import scala.collection.mutable.ListBuffer

case class Spline(segments: Seq[Segment]):
  lazy val continuous: Boolean =
    segments.zip(segments.drop(1)).forall((a, b) => a.end =~= b.start)

  lazy val closed: Boolean =
    if segments.isEmpty then true
    else segments.head.start =~= segments.last.end

  def asContinuous:Spline =
    if continuous then this
    else
      val lb = ListBuffer[Segment]()
      var s = segments.head
      lb += s
      for seg <- segments.tail do
        if s.end =~= seg.start then
          lb += s
        else
          s = Segment.Line(s.end, seg.start)
          lb += s
        s = seg
      Spline(lb.result())

  def asClosed:Spline =
    if closed then this
    else Spline(segments :+ Segment.Line(segments.last.end, segments.head.start))

  def project(p: Whit): Whit =

    def bsdist(s: Segment): Double =
      if s.bounds.contains(p) then 0.0 else s.bounds.sdist(p)

    var candidates = segments.sortBy(bsdist)
    var min = Double.MaxValue
    var ret = Whit(0, 0)
    while candidates.nonEmpty do
      val s = candidates.head
      val bdist = bsdist(s)
      if min > bdist then
        val pr = s.project(p)
        val dist = pr.sdist(p)
        if dist < min then
          min = dist
          ret = pr
      candidates = candidates.tail
    ret

  lazy val length: Double =
    segments.map(_.length).sum

  def size: Int = segments.size

  def transform(m: Matrix): Spline =
    Spline(segments.map(_.transform(m)))
