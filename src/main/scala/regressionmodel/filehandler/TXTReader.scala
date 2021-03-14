package regressionmodel.filehandler


class TXTReader(fileName: String) extends regressionmodel.filehandler.Reader(fileName) {

  override def verifyFormat(): Boolean = {
    val lineRgx = """(-?\d+\.?\d*)[;,]\s*(-?\d+\.?\d*)""".r
    for (line <- this.lines) {
      if (lineRgx.findFirstIn(line).isEmpty) {
        throw InvalidDataFormat("Line didn't have correct TXT Format \"XX.xx, YY.yy\"", line)
      }
    }
    true
  }
}
