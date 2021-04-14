package regressionmodel.gui

import org.scalafx.extras.offFXAndWait
import regressionmodel.GlobalVars
import regressionmodel.Main.stage
import regressionmodel.filehandler._
import regressionmodel.mathematics.{ExponentialRegression, LinearRegression}
import scalafx.embed.swing.SwingFXUtils
import scalafx.scene.SnapshotParameters
import scalafx.scene.control._
import scalafx.scene.image.WritableImage
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scalafx.scene.layout.BorderPane
import scalafx.scene.transform.Transform
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

class MainGUI extends BorderPane {


  val regressionTypeToggle = new ToggleGroup
  regressionTypeToggle.selectedToggle.onChange((_, oldVal, newVal) => {
    //If oldVal is null, it's the first (instantion) selection and theres no need to update anything
    if (oldVal != null) {
      newVal match {
        case menuItem: javafx.scene.control.RadioMenuItem =>
          Plot.regressionSeries.regressionObject = menuItem.getText.toLowerCase match {
            case "exponential" => ExponentialRegression
            case _ => LinearRegression
          }
          Plot.updateRegressionSeries()
        case _ =>
      }
    }
  })

  val dataFormatToggle = new ToggleGroup
  dataFormatToggle.selectedToggle.onChange((_, oldVal, _) => {
    //If oldVal is null, it's the first (instantion) selection and theres no need to update anything
    if (oldVal != null) {
      //This should return to the old value IF there is duplicate error!
      dataFormatToggle.getSelectedToggle match {
        case menuItem: javafx.scene.control.RadioMenuItem =>
          GlobalVars.leftCoordinateIsX = menuItem.getText.toLowerCase match {
            case "x;y" => true
            case _ => false
          }
        case _ =>
      }
      updateAllPlots()
    }
  })
  val styleToggle = new ToggleGroup
  styleToggle.selectedToggle.onChange({
    val key = styleToggle.getSelectedToggle match {
      case menuItem: javafx.scene.control.RadioMenuItem =>
        menuItem.getText.toLowerCase
      case _ => "NONE"
    }
    if (GlobalVars.styleOptions.contains(key)) {
      Plot.pointSeries.setStyle(GlobalVars.styleOptions(key))
    } else {
      println("Error in styleToggle.onChange()")
    }
  })

  private def newMenuItem(text: String, tuple: (Array[String], ToggleGroup), defaultValStr: String): Menu = {
    //This is used for the settings menu, to create a menu item with first item selected
    new Menu(text) {
      items = tuple._1.map(name => new RadioMenuItem(name.capitalize) {
        toggleGroup = tuple._2
        selected = name.toLowerCase == defaultValStr
      }).toList
    }
  }

  private def disableAndSelectDataFormat(disableString: String, selectText: String): Unit = {
    for (toggle <- this.dataFormatToggle.toggles) {
      toggle match {
        case radioMenuItem: javafx.scene.control.RadioMenuItem =>
          radioMenuItem.setDisable(radioMenuItem.getText == disableString)
          if (selectText == radioMenuItem.getText) {
            radioMenuItem.setSelected(true)
          }
        case _ =>
      }
    }
  }

  private def restoreDataFormatOptions(): Unit = {
    for (toggle <- this.dataFormatToggle.toggles) {
      toggle match {
        case radioMenuItem: javafx.scene.control.RadioMenuItem => radioMenuItem.setDisable(false)
        case _ =>
      }
    }
  }

  private def updateAllPlots(): Unit = {
    // First update all the points
    Plot.update()
    // Then update the limits
    Plot.updateLimits()
    // Then draw the regression line with given limits
    Plot.updateRegressionSeries()
  }

  private def checkForDuplicates(): Unit = {
    val boolTuple = Plot.checkForDuplicates
    boolTuple match {
      case (true, true) =>
        // Neither XY or YX format is available because of duplicates
        Dialogs.showError("Duplicate error!",
          "Your data was corrupted! (Reason: Duplicate X-values)",
          "Please choose another file.")
        restoreDataFormatOptions()
        Plot.clearPlot()
      case (false, true) =>
        // YX format contains duplicates, so we need to disable that button
        // and select "XY" format as default and show a message
        if (!GlobalVars.leftCoordinateIsX) {
          Dialogs.showInfo("Data format info",
            "Your data contained duplicates in Y;X format",
            "Solution: Automatically switched to X;Y format")
        }
        disableAndSelectDataFormat("Y;X", "X;Y") // This will also call onChange() which calls updates
      case (true, false) =>
        // XY format contains duplicates, so we need to disable that button
        // and select "YX" format as default and show a message
        if (GlobalVars.leftCoordinateIsX) {
          Dialogs.showInfo("Data format info",
            "Your data contained duplicates in X;Y format",
            "Solution: Automatically switched to Y;X format")
        }
        disableAndSelectDataFormat("X;Y", "Y;X") // This will also call onChange() which calls updates
      case (false, false) =>
        disableAndSelectDataFormat("", "") // This will enable both dataFormats
        updateAllPlots()
    }
  }

  val menuBar: MenuBar = new MenuBar() {
    val uniqueCheckMenuItem = new CheckMenuItem("Unique X [OFF]")
    uniqueCheckMenuItem.onAction = event => {
      event.getTarget match {
        case checkItem: javafx.scene.control.CheckMenuItem =>
          if (!checkItem.isSelected) { // We want to restore both options if it was selected
            restoreDataFormatOptions()
            uniqueCheckMenuItem.text = "Unique X [OFF]"
          } else {
            checkForDuplicates()
            uniqueCheckMenuItem.text = "Unique X [ON]"
          }
        case _ =>
      }
    }
    val open = new MenuItem("Open...")
    open.accelerator = new KeyCodeCombination(KeyCode.O, KeyCombination.ControlDown)
    open.onAction = _ => {
      val fileChooser = new FileChooser {
        title = "Select the datafile"
      }
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
        offFXAndWait {
          reader.load()
        }
        val points = reader.getDataPoints
        println("Successfully loaded data points!")
        if (points.length > 5000) {
          Dialogs.showWarning("Data size warning!",
            "This might take some time to draw all the dots...",
            s"Your data contains ${points.length} points")
        }
        // Check for duplicates
        Plot.dataPoints = points
        if (uniqueCheckMenuItem.isSelected) {
          checkForDuplicates()
        } else {
          updateAllPlots()
        }
      }
    }
    val save = new MenuItem("Save...")
    save.accelerator = new KeyCodeCombination(KeyCode.S, KeyCombination.ControlDown)
    save.onAction = _ => {
      val fileChooser = new FileChooser {
        title = "Image destination"
      }
      fileChooser.extensionFilters.addAll(
        new ExtensionFilter("PNG files", "*.png")
      )
      val filePath = fileChooser.showSaveDialog(stage)
      if (filePath != null) {
        val format = filePath.getName.takeRight(3)
        format match {
          case "png" =>
            try {
              val smallerSide = math.min(Plot.getWidth, Plot.getHeight)
              val scale = 1200 / smallerSide // The smaller side will always be ~1200px
              val sp = new SnapshotParameters {
                transform = Transform.scale(scale, scale)
              }
              // The 3 lines below this are from stack-overflow
              val img: WritableImage = Plot.snapshot(sp, null)
              val bufferedImage = SwingFXUtils.fromFXImage(img, null)
              new ImageWriter(filePath).saveImage(bufferedImage)
              Dialogs.showInfo("Image saved!", "Snapshot was saved successfully!", "")
            } catch {
              case e: Exception =>
                println("Something went wrong with ImageSave:")
                println(e.getMessage)
            }
          case _ =>
            Dialogs.showError("Wrong extension!",
              "Image has to be in PNG format",
              s"You chose format: '$format'")
        }
      }
    }
    val close = new MenuItem("Close")
    close.onAction = _ => {
      restoreDataFormatOptions()
      Plot.clearPlot()
    }
    val exit = new MenuItem("Exit")
    exit.onAction = _ => sys.exit(0)
    menus = List(
      new Menu("_File") {
        mnemonicParsing = true
        items = List(open, save, close, exit)
      },
      new Menu("_Settings") {
        mnemonicParsing = true
        items = List(
          newMenuItem("Regression type", (GlobalVars.regressionOptions.sorted, regressionTypeToggle), "linear"),
          newMenuItem("Data format", (GlobalVars.dataFormatOptions.sorted, dataFormatToggle), "x;y"),
          uniqueCheckMenuItem,
          new SeparatorMenuItem,
          new MenuItem("Point color") {
            onAction = _ => Dialogs.showColorMenu()
          },
          newMenuItem("Point style", (GlobalVars.styleOptions.keys.toArray.sorted, styleToggle), "circle"),
          new MenuItem("Point size") {
            onAction = _ => Dialogs.showSizeDialog()
          },
          new SeparatorMenuItem,
          new MenuItem("X-axis limits") {
            onAction = _ => Dialogs.showLimitDialog(true)
          },
          new MenuItem("Y-axis limits") {
            onAction = _ => Dialogs.showLimitDialog(false)
          },
          new SeparatorMenuItem,
          new CheckMenuItem("Dark mode [OFF]") {
            onAction = event => {
              event.getTarget match {
                case checkItem: javafx.scene.control.CheckMenuItem =>
                  if (!checkItem.isSelected) {
                    GlobalVars.myStage.getScene.getStylesheets.clear()
                    GlobalVars.myStage.getScene.getStylesheets.add("DefaultStyle.css")
                    this.text = "Dark mode [OFF]"
                  } else {
                    GlobalVars.myStage.getScene.getStylesheets.clear()
                    GlobalVars.myStage.getScene.getStylesheets.add("DarkMode.css")
                    this.text = "Dark mode [ON]"
                  }
                case _ =>
              }
            }
          }
        )
      },

      new Menu("_Help") {
        mnemonicParsing = true
        items = List(
          new MenuItem("About") {
            onAction = _ => Dialogs.showInfo("About", "This program was made by Matias",
              "This is a project for CS-C2120 course")
          }
        )
      }
    )
  }
  this.top = menuBar
  this.bottom = BottomPanel
  this.center = Plot
  Plot.clearPlot() //We want to clear the first initial value (After the legend has been created)
}

