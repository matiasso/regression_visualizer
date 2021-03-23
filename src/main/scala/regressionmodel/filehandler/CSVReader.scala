package regressionmodel.filehandler

class CSVReader(fileName: String) extends Reader(fileName) {

  override def verifyFileType: Boolean = fileName.toLowerCase.endsWith(".csv")
}
