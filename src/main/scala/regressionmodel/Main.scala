package regressionmodel

import regressionmodel.gui.MainGUI
import scalafx.application.JFXApp
import scalafx.scene.Scene


object Main extends JFXApp {
  // Created a separate class for the MainGUI
  GlobalVars.myView = new MainGUI()

  GlobalVars.myStage = new JFXApp.PrimaryStage {
    scene = new Scene(GlobalVars.myView) {
      stylesheets.add("DefaultStyle.css")
    }
    title.value = "Regression Model"
    width = 950
    height = 700
    minHeight = 400
    minWidth = 400
  }
}
