package geom

trait BoxModifier[M]:
  def modify(box: Box, modifier: M): Box

object BoxModifier:
  given BoxModifier[Insets]:
    def modify(box: Box, insets: Insets): Box =
      Box(box.x + insets.left, box.y + insets.top, box.w - insets.left - insets.right, box.h - insets.top - insets.bottom)

  given BoxModifier[Dim]:
    def modify(box: Box, dim: Dim): Box = Box(box.x + dim.w, box.y + dim.h, box.w, box.h)

  given BoxModifier[Box => Box]:
    def modify(box: Box, f: Box => Box): Box = f(box)