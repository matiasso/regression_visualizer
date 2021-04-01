package regressionmodel

import regressionmodel.gui.Plot
import regressionmodel.mathematics.{ExponentialRegression, LinearRegression}
import scalafx.application.JFXApp
import scalafx.scene.control.ToggleGroup


object GlobalVars {

  var myStage: JFXApp.PrimaryStage = new JFXApp.PrimaryStage()
  var leftCoordinateIsX: Boolean = true
  val styleOptions: Map[String, String] = Map(
    // These were done with https://svg-path-visualizer.netlify.app/ and LOTS of testing
    "dot" -> "-fx-background-radius: 4px; -fx-background-padding: 3px;",
    "rectangle" -> "-fx-shape: \"M1,1h1v1h-1Z\";",
    "triangle" -> "-fx-shape: \"M2,0 4,3.4641016, 0, 3.4641016 Z\";",
    "plus-sign" -> "-fx-shape: \"M1,1h1v1h1v1h-1v1h-1v-1h-1v-1h1Z\";",
    "star" -> "-fx-shape: \"M 0.0 10.0 L 3.0 3.0 L 10.0 0.0 L 3.0 -3.0 L 0.0 -10.0 L -3.0 -3.0 L -10.0 0.0 L -3.0 3.0 Z \";",
    "heart" -> "-fx-shape: \"M140 20C73 20 20 74 20 140c0 135 136 170 228 303 88-132 229-173 229-303 0-66-54-120-120-120-48 0-90 28-109 69-19-41-60-69-108-69z\";",
    "cross" -> "-fx-shape: \"M3,3 17,17 15,19 1,5 Z M16,3 1,17 3,19 18,5Z\";"
  )
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
    Plot.regressionSeries.regressionObject = regrTypeToggle.getSelectedToggle.asInstanceOf[javafx.scene.control.RadioMenuItem].getText.toLowerCase match {
      case "exponential" => ExponentialRegression
      case _ => LinearRegression
    }
    Plot.update()
  }
  val dataFormatToggle = new ToggleGroup
  dataFormatToggle.selectedToggle.onChange({
    //This should return to the old value IF there is duplicate error!
    leftCoordinateIsX = dataFormatToggle.getSelectedToggle.asInstanceOf[javafx.scene.control.RadioMenuItem].getText.toLowerCase match {
      case "x;y" => true
      //case "y;x" => false
      case _ => false
    }
    Plot.update()
  })
  val colorToggle = new ToggleGroup
  colorToggle.selectedToggle.onChange({
    val key = colorToggle.getSelectedToggle.asInstanceOf[javafx.scene.control.RadioMenuItem].getText.toLowerCase
    //This if sentence shouldn't be needed but its here just in case
    if (colorOptions.contains(key)) {
      Plot.pointSeries.setColor(s"-fx-background-color: ${colorOptions(key)};")
    }
  })
  val styleToggle = new ToggleGroup
  styleToggle.selectedToggle.onChange({
    val key = styleToggle.getSelectedToggle.asInstanceOf[javafx.scene.control.RadioMenuItem].getText.toLowerCase
    if (this.styleOptions.contains(key)) {
      Plot.pointSeries.setStyle(this.styleOptions(key))
    } else {
      println("A weird error occurred in styleToggle.onChange")
    }
  })

}
