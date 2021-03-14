package regressionmodel

import regressionmodel.GlobalVars
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import scalafx.scene.shape.{Circle, Rectangle, Shape}

object DataPoints {

  private var coordinatePoints = Array[PVector]()
  var radius = 1.5

  def getCoordinateXList: Array[Double] = this.coordinatePoints.map(v => v.x)

  def getCoordinateYList: Array[Double] = this.coordinatePoints.map(v => v.y)

  def setCoordinates(arr: Array[PVector]):Unit = this.coordinatePoints = arr

}
