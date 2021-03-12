package regressionmodel

import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import scalafx.scene.shape.{Circle, Rectangle, Shape}

object DataPoints {

  private var coordinatePoints = Array[PVector]()
  var radius = 1.5
  val styleOptions: Map[String, Shape] = Map("dot" -> new Circle(), "rectangle" -> new Rectangle())
  val colorOptions: Map[String, Color] = Map("red" -> Red, "green" -> Green, "blue" -> Blue, "purple" -> Purple,
                                            "yellow" -> Yellow, "black" -> Black, "orange" -> Orange, "cyan" -> Cyan)
  val regressionOptions: Map[String, String] = Map("linear" -> "linear", "exponential" -> "exponential")
  var style:Shape = styleOptions("dot")  //This might be useless later on

  def getCoordinateXList: Array[Double] = this.coordinatePoints.map(v => v.x)

  def getCoordinateYList: Array[Double] = this.coordinatePoints.map(v => v.y)

  def setCoordinates(arr: Array[PVector]):Unit = this.coordinatePoints = arr

}
