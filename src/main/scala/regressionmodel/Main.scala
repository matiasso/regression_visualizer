package regressionmodel

import regressionmodel.gui.MainGUI
import scalafx.application.JFXApp
import scalafx.scene.Scene


object Main extends JFXApp {
  val view = new MainGUI()

  stage = new JFXApp.PrimaryStage {
    scene = new Scene(view)
    title.value = "Regression Model"
    width = 800
    height = 500
  }
}
