package regressionmodel.filehandler

class CSVReader (fileName: String) extends Reader(fileName) {

  override def verifyFormat(): Boolean = {
    val lineRgx = """(-?\d+\.?\d*);\s?(-?\d+\.?\d*)""".r
    for (line <- this.lines){
      if (lineRgx.findFirstIn(line).isEmpty) {
        throw InvalidDataFormat("Line didn't have correct CSV Format \"XX.xx;YY.yy\"", line)
      }
    }
    true
  }
}
