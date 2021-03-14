package regressionmodel

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


}
