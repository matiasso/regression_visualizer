package regressionmodel

import scalafx.scene.chart.{NumberAxis, XYChart}
import scalafx.scene.control.ToggleGroup
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import scalafx.scene.shape.{Circle, Rectangle, Shape}

object GlobalVars {

  val styleOptions: Map[String, Shape] = Map("dot" -> new Circle(), "rectangle" -> new Rectangle())
  val colorOptions: Map[String, Color] = Map("red" -> Red, "green" -> Green, "blue" -> Blue, "purple" -> Purple,
    "yellow" -> Yellow, "black" -> Black, "orange" -> Orange, "cyan" -> Cyan)
  val regressionOptions: Map[String, String] = Map("linear" -> "linear", "exponential" -> "exponential")
  val csvSeparatorOptions: Map[String, Char] = Map("comma" -> ',', "semicolon" -> ';')
  val styleToggle = new ToggleGroup
  val colorToggle = new ToggleGroup
  val regrTypeToggle = new ToggleGroup
  val csvSeparatorToggle = new ToggleGroup

  var dataPoints:Array[PVector] = Array[PVector]()
  var pointRadius = 1.5
  var graphColor:Color = Purple
  val xAxis = new NumberAxis(-10, 10, 1)
  xAxis.setLabel("X")
  val yAxis = new NumberAxis(-10, 10, 1)
  yAxis.setLabel("Y")
  val pointSeries: XYChart.Series[Number, Number] = new XYChart.Series()
  val regrSeries: XYChart.Series[Number, Number] = new XYChart.Series()



}
