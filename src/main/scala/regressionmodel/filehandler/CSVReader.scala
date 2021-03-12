package regressionmodel.filehandler

import java.io.{BufferedReader, FileNotFoundException, FileReader, IOException}

class CSVReader (fileName: String) extends Reader {

  override def load() : Unit = {
    try {
      // opens an incoming character stream from the file
      val fileIn = new FileReader(fileName)
      // read line by line from the previous stream
      val linesIn = new BufferedReader(fileIn)
      // At this point, the streams should be open
      // so we must remember to close them.
      try {

        // Read the text from the stream line by line until the read line is null
        var oneLine = linesIn.readLine()
        while (oneLine != null) {
          println(oneLine)
          oneLine = linesIn.readLine()
        }
      } finally {
        // Close open streams
        fileIn.close()
        linesIn.close()
      }
    } catch {
      case notFound: FileNotFoundException => println(notFound.getMessage)
      case e: IOException => println(e.getMessage)
    }
  }

}
