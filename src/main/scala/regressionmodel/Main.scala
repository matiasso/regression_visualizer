package regressionmodel

import regressionmodel.gui.MainGUI
import scalafx.application.JFXApp
import scalafx.scene.Scene


object Main extends JFXApp {
  //I created my own class for the Graphical user interface part
  val view = new MainGUI()

  stage = new JFXApp.PrimaryStage {
    scene = new Scene(view) {
      stylesheets.add("DefaultStyle.css")
    }
    title.value = "Regression Model"
    width = 950
    height = 700
    minHeight = 400
    minWidth = 400
  }
  GlobalVars.myStage = stage
}
