package regressionmodel.filehandler

import regressionmodel.gui.{CustomDialogException, Dialogs, ExpandedDialogException}
import regressionmodel.{GlobalVars, PVector}

import java.io.{BufferedReader, FileNotFoundException, FileReader, IOException}
import scala.collection.mutable.ArrayBuffer

// This class currently handles both TXT and CSV since they are so similar, that it would cause repeated lines in code if I implemented both features in their own classes
abstract class Reader(fileName: String) {

  protected var invalidLines: ArrayBuffer[String] = ArrayBuffer[String]()
  protected var lines: Array[String] = Array[String]()

  //This method is mostly from A+ materials
  //It works for both CSV and TXT files
  def load(): Unit = {
    // verifyFileType just checks that the extension is correct
    if (!this.verifyFileType) {
      //I check the fileType with extension filters in the file chooser window so this should never occur, but it's here just in case
      throw new Exception(s"The filetype for $fileName is invalid!")
    }
    try {
      // opens an incoming character stream from the file
      val fileIn = new FileReader(fileName)
      // read line by line from the previous stream
      val linesIn = new BufferedReader(fileIn)
      // At this point, the streams should be open so we must remember to close them.
      try {
        // Read the text from the stream line by line until the read line is null
        var oneLine = linesIn.readLine()
        val lineBuffer = new ArrayBuffer[String]()
        while (oneLine != null) {
          // Use regex here to remove all not needed characters from the line
          val line = oneLine.replaceAll(raw"[^-\d;,.E]", "")
          if (line.nonEmpty) {
            lineBuffer += line
          }
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
    // Ask the user whether the data is in X;Y format or Y;X format, and if the X-coordinates should be unique or not
    val (isLeftX, isXUnique) = Dialogs.showTxtCsvFormatMenu()
    //This works for both TXT and CSV, so no need to override it in their subclasses
    val pointBuffer = new ArrayBuffer[PVector]()
    for (line <- lines) {
      val nums = line.replace(',', '.').split(';')
      if (nums.length == 2) {
        val xOpt = nums(0).trim.toDoubleOption
        val yOpt = nums(1).trim.toDoubleOption

        (xOpt, yOpt) match {
          case (Some(a), Some(b)) =>
            if (a.isFinite && b.isFinite) {
              val x = if (isLeftX) a else b
              val y = if (isLeftX) b else a
              if (isXUnique) {
                // If this is enabled, we want to first check if there already exists a coordinate with the given X coordinate
                if (pointBuffer.exists(p => p.x == x)) {
                  throw CustomDialogException("Duplicate error",
                    "Found duplicate for X coordinate: " + x,
                    "Check your data or read data again with Unique X disabled.")
                }
              }
              pointBuffer += new PVector(x, y)
            } else {
              invalidLines += line
            }
          case _ =>
            invalidLines += line
        }
      } else {
        invalidLines += line
      }
    }
    if (lines.length == 0) {
      throw CustomDialogException("Empty file!",
        "There was no proper data in the file you selected.",
        "Please choose another file.")
    }
    else if (invalidLines.size == lines.length) {
      throw ExpandedDialogException("Invalid data!",
        "No line in your data had the correct data format.",
        "Correct format is \"X;Y\" or \"Y;X\"",
        "Notice the semicolon separator! Examples of correct format:\n" + GlobalVars.correctFormatExamples)
    }
    pointBuffer.toArray
  }

  def getFaultyLines: Array[String] = this.invalidLines.toArray

  protected def verifyFileType: Boolean

}
