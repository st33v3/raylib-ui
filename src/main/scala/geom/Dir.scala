package geom

enum Dir(val dx: Int, val dy: Int):
  case N extends Dir(0, -1)
  case NE extends Dir(1, -1)
  case E extends Dir(1, 0)
  case SE extends Dir(1, 1)
  case S extends Dir(0, 1)
  case SW extends Dir(-1, 1)
  case W extends Dir(-1, 0)
  case NW extends Dir(-1, -1)

  def isMain: Boolean = dx == 0 || dy == 0
  def isDiagonal: Boolean = !isMain
  def isHorizontal: Boolean = dy == 0
  def isVertical: Boolean = dx == 0
  def opposite: Dir = Dir.arr((this.ordinal + 4) % 8)
  def next: Dir = Dir.arr((this.ordinal + 1) % 8)
  def prev: Dir = Dir.arr((this.ordinal + 7) % 8)
  def nextMain: Dir = Dir.arr(((this.ordinal & 6) + 2) % 8)
  def prevMain: Dir = Dir.arr(((this.ordinal & 6) + 6) % 8)

  def asDim: Dim = Dim(dx, dy)
  def assertMain(): Unit = if !isMain then throw IllegalArgumentException(s"Not a main direction: $this")
  def select(point: Whit) =
    assertMain()
    if dx == 0 then point.y else point.x
  def select(dim: Dim) =
    assertMain()
    if dx == 0 then dim.h else dim.w

object Dir:
  val main = List(N, E, S, W)
  val all = List(N, NE, E, SE, S, SW, W, NW)
  private val arr = all.toArray
