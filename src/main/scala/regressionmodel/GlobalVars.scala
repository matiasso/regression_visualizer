package regressionmodel

import regressionmodel.gui.Plot
import regressionmodel.mathematics.{ExponentialRegression, LinearRegression}
import scalafx.application.JFXApp
import scalafx.scene.control.ToggleGroup
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import scalafx.scene.shape.{Circle, Rectangle, Shape}


object GlobalVars {

  var myStage: JFXApp.PrimaryStage = new JFXApp.PrimaryStage()
  val styleOptions: Map[String, String] = Map("dot" -> "",
    "rectangle" -> "")
  val colorOptions: Map[String, String] = Map("red" -> "red",
    "green" -> "green",
    "blue" -> "blue",
    "purple" -> "purple",
    "yellow" -> "yellow",
    "black" -> "black",
    "orange" -> "orange",
    "cyan" -> "cyan")
  val regressionOptions: Array[String] = Array("linear", "exponential")
  val dataFormatOptions: Array[String] = Array("X;Y", "Y;X")
  val regrTypeToggle = new ToggleGroup
  regrTypeToggle.selectedToggle.onChange {
    Plot.regrObject = regrTypeToggle.getSelectedToggle.asInstanceOf[javafx.scene.control.RadioMenuItem].getText.toLowerCase match {
      case "exponential" => ExponentialRegression
      case _ => LinearRegression
    }
    Plot.updateData()
  }
  val dataFormatToggle = new ToggleGroup
  dataFormatToggle.selectedToggle.onChange({
    //This should return to the old value IF there is duplicate error!
    Plot.leftCoordinateIsX = dataFormatToggle.getSelectedToggle.asInstanceOf[javafx.scene.control.RadioMenuItem].getText.toLowerCase match {
      case "x;y" => true
      case "y;x" => false
    }
    Plot.updateData()
  })
  val styleToggle = new ToggleGroup
  val colorToggle = new ToggleGroup
  colorToggle.selectedToggle.onChange({
    val key = colorToggle.getSelectedToggle.asInstanceOf[javafx.scene.control.RadioMenuItem].getText.toLowerCase
    //This if sentence shouldn't be needed but its here just in case
    if (colorOptions.contains(key)){
      Plot.setPointStyle("-fx-background-color: " + colorOptions(key))
    }
  })

}
