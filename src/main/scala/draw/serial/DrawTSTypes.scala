package draw.serial

import com.scalatsi.TypescriptType.{TSLiteralString, TSUnion}
import com.scalatsi.TSType

object DrawTSTypes:
  given TSType[SegmentType] = TSType.alias("SegmentType", TSUnion(SegmentType.values.map(s => TSLiteralString(s.toString))))
  //val x = SegmentData(Hu.line, List((1.0, 2.0), (3.0, 4.0)))