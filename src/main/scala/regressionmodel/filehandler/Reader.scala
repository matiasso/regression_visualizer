package regressionmodel.filehandler

import regressionmodel.PVector

import java.io.{BufferedReader, FileNotFoundException, FileReader, IOException}
import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex.Match

abstract class Reader(fileName: String) {

  protected var lines:Array[String] = Array[String]()

  def load() : Unit = {
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

  def getDataPoints(leftIsX: Boolean): Array[PVector] = {
    //This works for both TXT and CSV
    val pointBuffer = new ArrayBuffer[PVector]()
    try {
      if (!this.verifyFormat()){
        return pointBuffer.toArray
      }
      //This try is probably useless, since we first verifyFormat() with the other method, but it's here just in case
      val lineRgx = """(-?\d+\.?\d*)[,;]\s*(-?\d+\.?\d*)""".r
      for (line <- lines) {
        val rgxM = lineRgx.findFirstMatchIn(line).get
        val pVector = if (leftIsX) new PVector(rgxM.group(1).toDouble, rgxM.group(2).toDouble) else
            new PVector(rgxM.group(2).toDouble, rgxM.group(1).toDouble)
        pointBuffer += pVector
      }
    } catch {
      case e: Throwable => throw e
    }
    pointBuffer.toArray
  }

  def verifyFormat() : Boolean

}
