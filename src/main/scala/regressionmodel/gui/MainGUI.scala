package regressionmodel.gui

import scalafx.scene.control.{Button, Menu, MenuBar, MenuItem, RadioMenuItem, ToggleGroup}
import scalafx.scene.layout.{BorderPane, StackPane}
import regressionmodel.DataPoints

class MainGUI extends BorderPane {

  val menuBar: MenuBar = new MenuBar() {
    val styleToggle = new ToggleGroup
    val colorToggle = new ToggleGroup
    val regrTypeToggle = new ToggleGroup
    menus = List(
      new Menu("File") {
        items = List(
          new MenuItem("Open..."),
          new MenuItem("Save..."))
      }, new Menu("Settings") {
        items = List(
          new Menu("Regression type") {
            items = DataPoints.regressionOptions.map(pair => new RadioMenuItem(pair._1.capitalize) {
              toggleGroup = regrTypeToggle
              selected = DataPoints.regressionOptions.head._1 == pair._1
            }).toList
          },
          new Menu("Graph color"){
            items = DataPoints.colorOptions.map(pair => new RadioMenuItem(pair._1.capitalize) {
              toggleGroup = colorToggle
              selected = DataPoints.colorOptions.head._1 == pair._1
            }).toList
          },
          new Menu("Point style") {
            items = DataPoints.styleOptions.map(pair => new RadioMenuItem(pair._1.capitalize) {
              toggleGroup = styleToggle
              selected = DataPoints.style == pair._2
            }).toList
          }
        )
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
