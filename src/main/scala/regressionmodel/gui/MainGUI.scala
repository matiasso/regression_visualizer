package regressionmodel.gui

import scalafx.scene.control.{Button, Menu, MenuBar, MenuItem, RadioMenuItem, ToggleGroup}
import scalafx.scene.layout.{BorderPane, StackPane}
import regressionmodel.DataPoints
import regressionmodel.Main.stage
import regressionmodel.filehandler._
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scalafx.stage.FileChooser

class MainGUI extends BorderPane {

  val menuBar: MenuBar = new MenuBar() {
    val styleToggle = new ToggleGroup
    val colorToggle = new ToggleGroup
    val regrTypeToggle = new ToggleGroup
    val open = new MenuItem("Open...")
    open.accelerator = new KeyCodeCombination(KeyCode.O, KeyCombination.ControlDown)
    open.onAction = e => {
      val fileChooser = new FileChooser
      val selectedFile = fileChooser.showOpenDialog(stage)
      //If the user cancels the selection, it will be null
      if (selectedFile != null){
        println("Selected: " + selectedFile.getAbsolutePath)
        selectedFile.getName.takeRight(3) match {
          case "txt" => {
            val txtReader = new TXTReader(selectedFile.getAbsolutePath)
            txtReader.load()
          }
          case "csv" => {
            val csvReader = new CSVReader(selectedFile.getAbsolutePath)
            csvReader.load()
          }
          case _ => println("Unknown file type!") //Throw exception and show message dialog here
        }
      }
    }
    val save = new MenuItem("Save...")
    save.accelerator = new KeyCodeCombination(KeyCode.S, KeyCombination.ControlDown)
    //save.onAction = e =>
    val exit = new MenuItem("Exit")
    exit.onAction = e => sys.exit(0)
    menus = List(
      new Menu("File") {
        items = List(open, save, exit)
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
