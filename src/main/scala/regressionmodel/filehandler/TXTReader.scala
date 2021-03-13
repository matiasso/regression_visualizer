package regressionmodel.filehandler

import regressionmodel.PVector

import java.io._
import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex.Match

class TXTReader (fileName: String) extends regressionmodel.filehandler.Reader {

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
        var lineBuffer = new ArrayBuffer[String]()
        while (oneLine != null) {
          lineBuffer += oneLine
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

  override var lines: Array[String] = new Array[String](0)

  override def getDataPoints(leftIsX: Boolean): Array[PVector] = {
    val pointBuffer = new ArrayBuffer[PVector]()
    try {
      val lineRgx = """^(\d+),(\d+)""".r
      for (line <- lines){
        val rgxMatch = lineRgx.findFirstMatchIn(line)
        val pVector = rgxMatch match {
          case None => throw InvalidDataFormat("The line didn't have correct 'X,Y' format.", line)
          case Some(m:Match) => if (leftIsX) new PVector(m.group(1).toInt, m.group(2).toInt) else
            new PVector(m.group(2).toInt, m.group(1).toInt)
        }
        pointBuffer += pVector
      }
    } catch {
      case e:Throwable => println(e.getMessage)
    }
    pointBuffer.toArray
  }
}
