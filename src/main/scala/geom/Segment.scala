package geom

// See https://github.com/Pomax/bezierjs

private [geom] case class Roots(r1: Double = Double.NaN, r2: Double = Double.NaN, r3: Double = Double.NaN):
  def toSeq: Seq[Double] = Seq(r1, r2, r3).filterNot(_.isNaN)

  def map(f: Double => Double, min: Double = Double.MinValue, max: Double = Double.MaxValue): Roots = Roots(
    if r1.isNaN || r1 < min ||  r1 > max then Double.NaN else f(r1),
    if r2.isNaN || r1 < min ||  r1 > max  then Double.NaN else f(r2),
    if r3.isNaN || r1 < min ||  r1 > max  then Double.NaN else f(r3),
  )

  def min(f: Double => Double, base: Double = Double.NaN, min: Double = Double.MinValue, max: Double = Double.MaxValue): Double =
    var m = base
    if !r1.isNaN && r1 >= min && r1 <= max then if m.isNaN then m = f(r1) else m = m min f(r1)
    if !r2.isNaN && r2 >= min && r2 <= max then if m.isNaN then m = f(r2) else m = m min f(r2)
    if !r3.isNaN && r3 >= min && r3 <= max then if m.isNaN then m = f(r3) else m = m min f(r3)
    m

  def max(f: Double => Double, base: Double = Double.NaN, min: Double = Double.MinValue, max: Double = Double.MaxValue): Double =
    var m = base
    if !r1.isNaN && r1 >= min && r1 <= max then if m.isNaN then m = f(r1) else m = m max f(r1)
    if !r2.isNaN && r2 >= min && r2 <= max then if m.isNaN then m = f(r2) else m = m max f(r2)
    if !r3.isNaN && r3 >= min && r3 <= max then if m.isNaN then m = f(r3) else m = m max f(r3)
    m

sealed trait Segment:
  val start: Whit
  val end: Whit
  def pointX(t: Double): Double
  def pointY(t: Double): Double
  def point(t: Double): Whit = Whit(pointX(t), pointY(t))
  def bounds: Box
  def length: Double
  def project(other: Whit): Whit =
    val (_, i) = lut.zipWithIndex.minBy(p => p._1.sdist(other))
    var t = (i - 1).toDouble / lut.length
    val t2 = (i + 1).toDouble / lut.length
    val step = 0.1 / lut.length
    var ret = 0.0
    var min = Double.MaxValue
    while t < t2 do
      val dist = pointSdist(pointX(t), pointY(t), other.x, other.y)
      if dist < min then
        min = dist
        ret = t
      t += step
    ret = ret min 1.0 max 0.0
    point(ret)

  def derivativeX(t: Double): Double
  def derivativeY(t: Double): Double
  def derivative(t: Double): Whit = Whit(derivativeX(t), derivativeY(t))

  protected lazy val lut: Array[Whit] =
    val n = (length / 5).toInt min 4
    Array.tabulate(n)(i => point(i.toDouble / n))

object Segment:
  
  case class Line(start: Whit, end: Whit) extends Segment:
    def pointX(t: Double): Double = start.x + (end.x - start.x) * t
    def pointY(t: Double): Double = start.y + (end.y - start.y) * t
    val bounds: Box =
      val x1 = start.x min end.x
      val y1 = start.y min end.y
      val x2 = start.x max end.x
      val y2 = start.y max end.y
      Box(x1, y1, x2 - x1, y2 - y1)


    lazy val rootsX: Roots = roots(true)
    lazy val rootsY: Roots = roots(false)

    private final inline def roots(inline x: Boolean) =
      val a = inline if x then start.x else start.y
      val b = inline if x then end.x else end.y
      if a =~= b then Roots() else Roots(a/(a - b))

    lazy val length: Double =
      Math.sqrt(pointSdist(start.x, start.y, end.x, end.y))

    override def project(other: Whit): Whit =
      val l = start.sdist(end)
      if l == 0 then start
      else
        val t = ((other -- start) dot (end -- start)) / l
        point(t)

    def derivativeX(t: Double): Double = end.x - start.x
    def derivativeY(t: Double): Double = end.y - start.y

  case class Quad(start: Whit, cp: Whit, end: Whit) extends Segment:

    private [geom] lazy val deriv = Line(((cp -- start)*2).asWhit, ((end -- cp)*2).asWhit)

    def pointX(t: Double): Double =
      start.x + t * (2 * cp.x - 2 * start.x + t * (start.x - 2 * cp.x + end.x))

    def pointY(t: Double): Double =
      start.y + t * (2 * cp.y - 2 * start.y + t * (start.y - 2 * cp.y + end.y))

    override def derivativeX(t: Double): Double = deriv.pointX(t)
    override def derivativeY(t: Double): Double = deriv.pointY(t)

    lazy val rootsX: Roots = roots(true)
    lazy val rootsY: Roots = roots(false)

    private final inline def roots(inline x: Boolean) =
      val a = inline if x then start.x else start.y
      val b = inline if x then cp.x else cp.y
      val c = inline if x then end.x else end.y
      val d = a - 2 * b + c
      if !(d =~= 0.0) then
        val m1 = -Math.sqrt(b * b - a * c)
        val m2 = -a + b
        val v1 = -(m1 + m2) / d
        val v2 = -(-m1 + m2) / d
        Roots(v1, v2)
      else
        if !(b =~= c) then Roots((2 * b - c) / (2 * (b - c)))
        else Roots()

    lazy val bounds: Box =
      var mnx = start.x min end.x
      mnx = deriv.rootsX.min(t => pointX(t), mnx, 0, 1)
      var mny = start.y min end.y
      mny = deriv.rootsY.min(t => pointY(t), mny, 0, 1)
      var mxx = start.x max end.x
      mxx = deriv.rootsX.max(t => pointX(t), mxx, 0, 1)
      var mxy = start.y max end.y
      mxy = deriv.rootsY.max(t => pointY(t), mxy, 0, 1)
      Box.fromCorners(mnx, mny, mxx, mxy)

    lazy val length: Double = geom.length(deriv)

  case class Cubic(start: Whit, cp1: Whit, cp2: Whit, end: Whit) extends Segment:
    
    override def pointX(t: Double): Double =
      start.x + t * (3 * cp1.x - 3 * start.x + t * (3 * cp2.x - 6 * cp1.x + 3 * start.x + t * (end.x - 3 * cp2.x + 3 * cp1.x - start.x)))

    override def pointY(t: Double): Double =
      start.y + t * (3 * cp1.y - 3 * start.y + t * (3 * cp2.y - 6 * cp1.y + 3 * start.y + t * (end.y - 3 * cp2.y + 3 * cp1.y - start.y)))    
    
    private [geom] lazy val deriv = Quad(((cp1 -- start)*3).asWhit, ((cp2 -- cp1)*3).asWhit, ((end -- cp2)*3).asWhit)

    override def derivativeX(t: Double): Double = deriv.pointX(t)
    override def derivativeY(t: Double): Double = deriv.pointY(t)
    
    lazy val bounds: Box =
      var minX = start.x min end.x
      minX = deriv.rootsX.min(t => pointX(t), minX, 0, 1)
      minX = deriv.deriv.rootsX.min(t => pointX(t), minX, 0, 1)
      var minY = start.y min end.y
      minY = deriv.rootsY.min(t => pointY(t), minY, 0, 1)
      minY = deriv.deriv.rootsY.min(t => pointY(t), minY, 0, 1)
      var maxX = start.x max end.x
      maxX = deriv.rootsX.max(t => pointX(t), maxX, 0, 1)
      maxX = deriv.deriv.rootsX.max(t => pointX(t), maxX, 0, 1)
      var maxY = start.y max end.y
      maxY = deriv.rootsY.max(t => pointY(t), maxY, 0, 1)
      maxY = deriv.deriv.rootsY.max(t => pointY(t), maxY, 0, 1)
      Box.fromCorners(minX, minY, maxX, maxY)

    lazy val length: Double = geom.length(deriv)
