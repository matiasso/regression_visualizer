package regressionmodel.filehandler

class TXTReader(fileName: String) extends regressionmodel.filehandler.Reader(fileName) {

  override def verifyFileType: Boolean = fileName.toLowerCase.endsWith(".txt")
}
