package regressionModel

import scalafx.application.JFXApp
import scalafx.scene.Scene


object Main extends JFXApp {
  val view = new MainGUI()

  stage = new JFXApp.PrimaryStage {
    scene = new Scene(view)
    title.value = "Hello Stage"
    width = 800
    height = 500
  }
}
