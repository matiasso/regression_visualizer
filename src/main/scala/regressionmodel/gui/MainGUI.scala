package regressionmodel.gui

import regressionmodel.GlobalVars
import regressionmodel.Main.stage
import regressionmodel.filehandler._
import scalafx.scene.control._
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scalafx.scene.layout.{BorderPane, StackPane}
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

class MainGUI extends BorderPane {

  def newMenuItem(text:String, tuple: (Array[String], ToggleGroup)): Menu = {
    //This is used for the settings menu, to create a menu item with first item selected
    new Menu(text) {
      items = tuple._1.map(name => new RadioMenuItem(name.capitalize) {
        toggleGroup = tuple._2
        selected = name == tuple._1.head
      }).toList
    }
  }

  val menuBar: MenuBar = new MenuBar() {
    val open = new MenuItem("Open...")
    open.accelerator = new KeyCodeCombination(KeyCode.O, KeyCombination.ControlDown)
    open.onAction = e => {
      val fileChooser = new FileChooser
      fileChooser.setTitle("Select the datafile")
      fileChooser.extensionFilters.addAll(
        //Why are these seperate ?? combine them later
        new ExtensionFilter("Text and CSV files", Seq("*.txt", "*.csv")),
        //new ExtensionFilter("CSV Files", "*.csv")
      )
      val selectedFile = fileChooser.showOpenDialog(stage)
      //If the user cancels the selection, it will be null
      if (selectedFile != null) {
        println("Selected: " + selectedFile.getAbsolutePath)
        val reader = selectedFile.getName.takeRight(3) match {
          case "txt" => new TXTReader(selectedFile.getAbsolutePath)
          case "csv" => new CSVReader(selectedFile.getAbsolutePath)
          //These are the only cases since the extensionFilter limits to these types only
          //That's why we don't need "case _ =>" here
        }
        reader.load()
        reader.getDataPoints(true)
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
          newMenuItem("Regression type", (GlobalVars.regressionOptions.map(_._1).toArray, GlobalVars.regrTypeToggle)),
          newMenuItem("CSV Separator", (GlobalVars.csvSeparatorOptions.map(_._1).toArray, GlobalVars.csvSeparatorToggle)),
          newMenuItem("Graph color", (GlobalVars.colorOptions.map(_._1).toArray, GlobalVars.colorToggle)),
          newMenuItem("Point style", (GlobalVars.styleOptions.map(_._1).toArray, GlobalVars.styleToggle))
        )
      }, new Menu("Help") {
        items = List(
          new MenuItem("About")
        )
      }
    )
  }
  this.top = menuBar
  this.right = new SidePanel()
  this.center = new StackPane() {
    children = new Button("testi") {
      prefHeight = 300
      prefWidth = 600
    }
  }
}
