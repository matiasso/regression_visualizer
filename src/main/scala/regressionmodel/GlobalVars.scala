package regressionmodel

import regressionmodel.gui.MainGUI
import scalafx.application.JFXApp


object GlobalVars {

  var myView: MainGUI = _
  var myStage: JFXApp.PrimaryStage = _
  val correctFormatExamples = "1;2\n1; 2\n1 ;2\n1 ; 2\n2.5 ;4\n3; 5.6725\n4.98765;6.1234\n5E-3;10E3"
  val textUnknownCoefficients: String = ""
  val textForGraphLabel = "Graph: "
  val textRSquared = "R² value: "
  val styleOptions: Map[String, String] = Map(
    // These were done with https://svg-path-visualizer.netlify.app/ with LOTS of experimenting
    "circle" -> "-fx-shape: \"M 25,50 a25,25 0 1,1 50,0 a25,25 0 1,1 -50,0\";",
    "rectangle" -> "-fx-shape: \"M1,1h1v1h-1Z\";",
    "triangle" -> "-fx-shape: \"M2,0 4,3.4641016, 0, 3.4641016 Z\";",
    "plus sign" -> "-fx-shape: \"M1,1h1v1h1v1h-1v1h-1v-1h-1v-1h1Z\";",
    "star" -> "-fx-shape: \"M 0.0 10.0 L 3.0 3.0 L 10.0 0.0 L 3.0 -3.0 L 0.0 -10.0 L -3.0 -3.0 L -10.0 0.0 L -3.0 3.0 Z \";",
    "heart" -> "-fx-shape: \"M140 20C73 20 20 74 20 140c0 135 136 170 228 303 88-132 229-173 229-303 0-66-54-120-120-120-48 0-90 28-109 69-19-41-60-69-108-69z\";",
    "cross" -> "-fx-shape: \"M0,0l5,5l5-5l2,2l-5,5l5,5l-2,2l-5-5l-5,5l-2-2l5,-5l-5-5Z\";",
    "banana" -> "-fx-shape: \"M 8,223 c 0,0 143,3 185,-181 c 2,-11 -1,-20 1,-33 h 16 c 0,0 -3,17 1,30 c 21,68 -4,242 -204,196 L 8,223 z M 8,230 c 0,0 188,40 196,-160\";"
  )
  val regressionOptions: Array[String] = Array("linear", "exponential")
}
