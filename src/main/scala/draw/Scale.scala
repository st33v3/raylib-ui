package draw

case class Scale(pigments: Seq[Pigment], stops: Seq[Double]):
  assert(pigments.length == stops.length)
  assert(stops.zip(stops.drop(1)).forall(_ < _), "Stops must be monotonically increasing")
  assert(stops.forall(s => s >= 0 && s <= 1), "Stops must be in the range [0, 1]")

  def interpolate(t: Double): Pigment =
    if t <= stops.head then pigments.head
    else if t >= stops.last then pigments.last
    else
      val i = stops.indexWhere(_ > t)
      val t1 = stops(i - 1)
      val t2 = stops(i)
      val p1 = pigments(i - 1)
      val p2 = pigments(i)
      val u = (t - t1) / (t2 - t1)
      p1.tint(1 - u).tint(p2.tint(u))

object Scale:
  def twoColor(p1: Pigment, p2: Pigment): Scale = Scale(Seq(p1, p2), Seq(0, 1))