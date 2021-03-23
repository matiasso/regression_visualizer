package regressionmodel.filehandler

import regressionmodel.PVector
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.scene.control.Alert.AlertType

import java.io.{BufferedReader, FileNotFoundException, FileReader, IOException}
import scala.collection.mutable.ArrayBuffer

abstract class Reader(fileName: String) {

  protected var lines: Array[String] = Array[String]()

  //This method is from A+ materials
  //It works for both CSV and TXT files
  def load(): Unit = {
    if (!this.verifyFileType){
      //This should never occur, but it's here just in case
      throw new Exception(s"The filetype for $fileName is invalid!")
    }
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
        var lineBuffer = new ArrayBuffer[String]()
        while (oneLine != null) {
          lineBuffer += oneLine.strip()
          oneLine = linesIn.readLine()
        }
        this.lines = lineBuffer.toArray
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

  def getDataPoints: Array[PVector] = {
    //This works for both TXT and CSV
    val pointBuffer = new ArrayBuffer[PVector]()
    //Old regex that I used
    //val lineRgx = """(-?\d+\.?\d*)[,;]\s*(-?\d+\.?\d*)""".r
    for (line <- lines){
      val nums = line.replace(',', '.').replace("\uFEFF", "").split(';')
      if (nums.length == 2){
        val x = nums(0).trim.toDoubleOption
        val y = nums(1).trim.toDoubleOption

        (x, y) match {
          case (Some(v), Some(w)) => pointBuffer += new PVector(v, w)
          case _ =>
            nums(0).foreach(c => println(s"C: $c INT ${c.toInt}"))
            nums(1).foreach(c => println(s"C: $c INT ${c.toInt}"))
            showWarning("Incorrect number format!",
              s"The line '$line' had incorrect format!",
              "Format should be 'XX.xx;YY.yy")
        }
      } else {
        this.showWarning("Incorrect data format",
          s"The line '$line' had incorrect format!",
          "Format should be 'XX.xx;YY.yy (semicolon separator)")
      }
    }
    pointBuffer.toArray
  }

  def verifyFileType: Boolean

  def showWarning(titleStr: String, header: String, content: String): Option[ButtonType]= {
    new Alert(AlertType.Warning) {
      title = titleStr
      headerText = header
      contentText = content
    }.showAndWait()
  }

}
