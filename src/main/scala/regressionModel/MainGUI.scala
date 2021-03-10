package regressionModel

import scalafx.scene.control.{Button, Menu, MenuBar, MenuItem}
import scalafx.scene.layout.{AnchorPane, BorderPane, StackPane}

class MainGUI extends BorderPane {

  val menuBar = new MenuBar() {
    menus = List(
      new Menu("File") {
        items = List(
        new MenuItem("Open..."))
      }, new Menu("Help") {
        items = List(
            new MenuItem("About")
        )
      }
    )
  }
  this.top = menuBar
  this.right =  new SidePanel()
  this.center = new StackPane() {
    children = new Button("testi") {
      prefHeight = 300
      prefWidth = 600
    }
  }
}
