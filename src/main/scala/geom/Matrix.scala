package geom

case class Matrix(m00: Double, m10: Double, m01: Double, m11: Double, m02: Double, m12: Double):

  def *(that: Matrix): Matrix =
    Matrix(m00 * that.m00 + m01 * that.m10, m10 * that.m00 + m11 * that.m10,
      m00 * that.m01 + m01 * that.m11, m10 * that.m01 + m11 * that.m11,
      m00 * that.m02 + m01 * that.m12 + m02, m10 * that.m02 + m11 * that.m12 + m12)

  def *(p: Whit): Whit = transformWhit(p.x, p.y)
  def *(d: Dim): Dim = transformDim(d.w, d.h)

  lazy val inverse: Matrix =
    val det = m00 * m11 - m01 * m10
    if det =~= 0.0 then throw new Exception("Non-invertible matrix. Determinant is " + det)
    Matrix(m11 / det, -m10 / det,
      -m01 / det, m00 / det,
      (m01 * m12 - m11 * m02) / det,
      (m10 * m02 - m00 * m12) / det)

  def =~=(other: Matrix): Boolean =
    m00 =~= other.m00 && m10 =~= other.m10 && m01 =~= other.m01 && m11 =~= other.m11 && m02 =~= other.m02 && m12 =~= other.m12

  def transformX(x: Double, y: Double): Double = m00 * x + m01 * y + m02
  def transformY(x: Double, y: Double): Double = m10 * x + m11 * y + m12
  def transformWhit(x: Double, y: Double): Whit = Whit(transformX(x, y), transformY(x, y))
  def transformDim(w: Double, h: Double): Dim = Dim(transformX(w, h), transformY(w, h))

object Matrix:
  def move(dim: Dim): Matrix = move(dim.w, dim.h)
  def move(x: Double, y: Double): Matrix = Matrix(1.0, 0.0, 0.0, 1.0, x, y)
  def scale(dim: Dim): Matrix = scale(dim.w, dim.h)
  def scale(f: Double): Matrix = scale(f, f)
  def scale(x: Double, y: Double): Matrix = Matrix(x, 0, 0, y, 0, 0)

  protected def rotate(c: Double, s: Double): Matrix = Matrix(c, -s, s, c, 0, 0)

  def rotate(dim: Dim): Matrix =
    val l = dim.length
    if l == 0 then Matrix.identity
    else rotate(dim.w / l, dim.h / l)

  def rotate(theta: Double): Matrix =
    val c = Math.cos(theta)
    val s = Math.sin(theta)
    rotate(c, s)

  def shear(dim: Dim): Matrix = shear(dim.w, dim.h)
  def shear(x: Double, y: Double): Matrix = Matrix(1.0, x, y, 1.0, 0, 0)

  private val invSqrt2 = 1.0 / Math.sqrt(2.0)

  def rotate(dir: Dir): Matrix =
    dir match
      case Dir.NE => rotate(invSqrt2, invSqrt2)
      case Dir.SE => rotate(invSqrt2, -invSqrt2)
      case Dir.SW => rotate(-invSqrt2, -invSqrt2)
      case Dir.NW => rotate(-invSqrt2, invSqrt2)
      case _ => quadrantRotate(4 - dir.ordinal / 2) //Only main directions remain

  def quadrantRotate(quadrants: Int): Matrix =
    quadrants & 3 match
      case 1 => rotate(0.0, 1.0)
      case 2 => rotate(-1.0, 0.0)
      case 3 => rotate(0.0, -1.0)
      case _ => Matrix.identity

  val identity: Matrix = Matrix(1, 0, 0, 1, 0, 0)
