package regressionmodel.filehandler

import regressionmodel.gui.Dialogs
import regressionmodel.{GlobalVars, PVector}
import scalafx.scene.control.Alert.AlertType

import java.io.{BufferedReader, FileNotFoundException, FileReader, IOException}
import scala.collection.mutable.ArrayBuffer

abstract class Reader(fileName: String) {

  protected var lines: Array[String] = Array[String]()

  //This method is from A+ materials
  //It works for both CSV and TXT files
  def load(): Unit = {
    if (!this.verifyFileType) {
      //I check the fileType with extension filters in the file chooser window
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
        val lineBuffer = new ArrayBuffer[String]()
        while (oneLine != null) {
          // Use regex here to remove all not needed characters from the line
          lineBuffer += oneLine.replaceAll(raw"[^-\d;,.E]", "")
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
    //This works for both TXT and CSV, so no need to override it in subclasses
    val pointBuffer = new ArrayBuffer[PVector]()
    //Old regex that I used
    //val lineRgx = """(-?\d+\.?\d*)[,;]\s*(-?\d+\.?\d*)""".r
    var incorrectCount = 0
    for (line <- lines) {
      val nums = line.replace(',', '.').split(';')
      if (nums.length == 2) {
        val x = nums(0).trim.toDoubleOption
        val y = nums(1).trim.toDoubleOption

        (x, y) match {
          case (Some(v), Some(w)) =>
            pointBuffer += new PVector(v, w)
          case _ =>
            incorrectCount += 1
        }
      } else {
        incorrectCount += 1
      }
    }
    if (lines.length == 0) {
      Dialogs.showError("Empty file!",
        "There was no proper data in the file you selected.",
        "Please choose another file.")
    }
    else if (incorrectCount == lines.length) {
      Dialogs.showDialogWithExpandedText(AlertType.Error,
        "Invalid data!",
        "No line in your data had the correct data format.",
        "Correct format is \"X;Y\" or \"Y;X\"",
        "Notice the semicolon separator! Examples of correct format:\n" + GlobalVars.correctFormatExamples)
    } else if (incorrectCount > 0) {
      Dialogs.showDialogWithExpandedText(AlertType.Warning,
        "File contained invalid data!",
        s"$incorrectCount/${lines.length} of your lines had incorrect format",
        "Correct format is \"X;Y\" or \"Y;X\"",
        "Notice the semicolon separator! Examples of correct format:\n" + GlobalVars.correctFormatExamples)
    }
    pointBuffer.toArray
  }

  def verifyFileType: Boolean

}
