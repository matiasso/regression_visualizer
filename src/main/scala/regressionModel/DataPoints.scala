package regressionModel

import scalafx.scene.paint.Color._

class DataPoints {

  private var coordinatePoints = Array[PVector]()
  var color = Red
  var style = styleOptions.dot
  var radius = 1.5

  def getCoordinateXList: Array[Double] = this.coordinatePoints.map(v => v.x)

  def getCoordinateYList: Array[Double] = this.coordinatePoints.map(v => v.y)

  def setCoordinates(arr: Array[PVector]) = this.coordinatePoints = arr

  object styleOptions {
    val dot = "dot"
    val cross = "cross"
    val triangle = "triangle"
  }

}
