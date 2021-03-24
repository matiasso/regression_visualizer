package regressionmodel.gui

import regressionmodel.GlobalVars
import regressionmodel.Main.stage
import regressionmodel.filehandler._
import scalafx.geometry.Insets
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control._
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scalafx.scene.layout.{BorderPane, GridPane}
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

class MainGUI extends BorderPane {


  def newMenuItem(text: String, tuple: (Array[String], ToggleGroup)): Menu = {
    //This is used for the settings menu, to create a menu item with first item selected
    new Menu(text) {
      items = tuple._1.map(name => new RadioMenuItem(name.capitalize) {
        toggleGroup = tuple._2
        selected = name == tuple._1.head
      }).toList
    }
  }

  def showLimitDialog(xDialog: Boolean): Unit = {
    val dialog = new Dialog[LimitResult]() {
      title = "Plot limits for " + (if (xDialog) "X" else "Y") + "-axis"
      headerText = "Select the limits for the graph"
    }

    val okButtonType = new ButtonType("OK", ButtonData.OKDone)
    val cancelButtonType = new ButtonType("Clear", ButtonData.CancelClose)
    dialog.dialogPane().getButtonTypes.addAll(okButtonType, cancelButtonType)

    val limA = new TextField() {
      promptText = "Enter double here"
    }
    val limB = new TextField() {
      promptText = "Enter double here"
    }
    val padAndGap = 10
    val grid = new GridPane() {
      hgap = padAndGap
      vgap = padAndGap
      padding = Insets(padAndGap, padAndGap, padAndGap, padAndGap)
      addRow(0, new Label("Lower bound:"), limA)
      addRow(1, new Label("Upper bound:"), limB)
    }
    val okButton = dialog.dialogPane().lookupButton(okButtonType)
    okButton.setDisable(true)

    //Add checks here for the double format, just as an extra feature :)
    def checkEnableOkButton(): Unit = {
      //Enable the button ONLY if both inputs are in Double format
      val doubleOptA = limA.text.value.toDoubleOption
      val doubleOptB = limB.text.value.toDoubleOption
      (doubleOptA, doubleOptB) match {
        case (Some(a), Some(b)) => okButton.setDisable(a >= b) //if a >= b then disable the button
        case _ => okButton.setDisable(true)
      }
    }

    limA.text.onChange { (_, _, _) =>
      checkEnableOkButton()
    }
    limB.text.onChange { (_, _, _) =>
      checkEnableOkButton()
    }

    dialog.dialogPane().setContent(grid)
    dialog.resultConverter = dButton =>
      if (dButton == okButtonType)
        LimitResult(limA.text().toDoubleOption, limB.text().toDoubleOption)
      else
        null
    val result = dialog.showAndWait()
    result match {
      case Some(LimitResult(a, b)) =>
        println(s"User gave inputs $a and $b for limits")
        //Send these values to the Plot graph!
        if (xDialog)
          Plot.setLimitsX(a, b)
        else
          Plot.setLimitsY(a, b)
      case None => println("Received None for the plot limits")
        //Empty the values in plot
        if (xDialog)
          Plot.setLimitsX(None, None)
        else
          Plot.setLimitsY(None, None)
    }
  }


  val menuBar: MenuBar = new MenuBar() {
    val open = new MenuItem("Open...")
    open.accelerator = new KeyCodeCombination(KeyCode.O, KeyCombination.ControlDown)
    open.onAction = e => {
      val fileChooser = new FileChooser
      fileChooser.setTitle("Select the datafile")
      fileChooser.extensionFilters.addAll(
        new ExtensionFilter("Text and CSV files", Seq("*.txt", "*.csv"))
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
        Plot.dataPoints.clear()
        //Since it's observableBuffer it'll auto-update
        Plot.dataPoints.addAll(reader.getDataPoints)
        println("Successfully loaded data points!")
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
      },

      new Menu("Settings") {
        items = List(
          newMenuItem("Regression type", (GlobalVars.regressionOptions.keys.toArray, GlobalVars.regrTypeToggle)),
          newMenuItem("Graph color", (GlobalVars.colorOptions.keys.toArray, GlobalVars.colorToggle)),
          newMenuItem("Point style", (GlobalVars.styleOptions.keys.toArray, GlobalVars.styleToggle)),
          newMenuItem("Data format", (GlobalVars.dataFormatOptions.keys.toArray, GlobalVars.dataFormatToggle)),
          new MenuItem("X-axis limits") {
            onAction = e => showLimitDialog(true)
          },
          new MenuItem("Y-axis limits") {
            onAction = e => showLimitDialog(false)
          }
        )
      },

      new Menu("Help") {
        items = List(
          new MenuItem("About")
        )
      }
    )
  }
  this.top = menuBar
  this.bottom = SidePanel
  this.center = Plot

}

