package regressionmodel

import scalafx.application.JFXApp


object GlobalVars {

  var myStage: JFXApp.PrimaryStage = new JFXApp.PrimaryStage()
  var leftCoordinateIsX: Boolean = true
  val styleOptions: Map[String, String] = Map(
    // These were done with https://svg-path-visualizer.netlify.app/ with LOTS of experimenting
    "dot" -> "-fx-background-radius: 4px; -fx-background-padding: 3px;",
    "rectangle" -> "-fx-shape: \"M1,1h1v1h-1Z\";",
    "triangle" -> "-fx-shape: \"M2,0 4,3.4641016, 0, 3.4641016 Z\";",
    "plus-sign" -> "-fx-shape: \"M1,1h1v1h1v1h-1v1h-1v-1h-1v-1h1Z\";",
    "star" -> "-fx-shape: \"M 0.0 10.0 L 3.0 3.0 L 10.0 0.0 L 3.0 -3.0 L 0.0 -10.0 L -3.0 -3.0 L -10.0 0.0 L -3.0 3.0 Z \";",
    "heart" -> "-fx-shape: \"M140 20C73 20 20 74 20 140c0 135 136 170 228 303 88-132 229-173 229-303 0-66-54-120-120-120-48 0-90 28-109 69-19-41-60-69-108-69z\";",
    "cross" -> "-fx-shape: \"M0,0l5,5l5-5l2,2l-5,5l5,5l-2,2l-5-5l-5,5l-2-2l5,-5l-5-5Z\";"
  )
  val regressionOptions: Array[String] = Array("linear", "exponential")
  val dataFormatOptions: Array[String] = Array("X;Y", "Y;X")
}
