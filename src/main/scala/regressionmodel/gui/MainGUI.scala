package regressionmodel.gui

import org.scalafx.extras.offFXAndWait
import regressionmodel.GlobalVars
import regressionmodel.Main.stage
import regressionmodel.filehandler._
import regressionmodel.mathematics.{ExponentialRegression, LinearRegression}
import scalafx.embed.swing.SwingFXUtils
import scalafx.scene.SnapshotParameters
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.image.WritableImage
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scalafx.scene.layout.BorderPane
import scalafx.scene.transform.Transform
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

class MainGUI extends BorderPane {


  // Define the regression toggle for settings menu
  private val regressionTypeToggle = new ToggleGroup
  regressionTypeToggle.selectedToggle.onChange((_, oldVal, newVal) => {
    //If oldVal is null, it's the first (instantion) selection and theres no need to update anything
    if (oldVal != null) {
      newVal match {
        case menuItem: javafx.scene.control.RadioMenuItem =>
          Plot.regressionSeries.regressionInstance = menuItem.getText.toLowerCase match {
            case "exponential" => new ExponentialRegression()
            case _ => new LinearRegression()
          }
          Plot.updateRegressionSeries()
        case _ =>
      }
    }
  })


  // Define the style toggle (shape toggle) for settings menu
  private val styleToggle = new ToggleGroup
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

  // A helper method for creating new sub-menus
  private def newMenuItem(text: String, tuple: (Array[String], ToggleGroup), defaultValStr: String): Menu = {
    //This is used for the settings menu, to create a menu item with first item selected
    new Menu(text) {
      items = tuple._1.map(name => new RadioMenuItem(name.capitalize) {
        toggleGroup = tuple._2
        selected = name.toLowerCase == defaultValStr
      }).toList
    }
  }

  private def updateAllPlots(): Unit = {
    // First update all the points
    Plot.updateDataPoints()
    // Then update the limits
    Plot.updateLimits()
    // Then draw the regression line with given limits
    Plot.updateRegressionSeries()
  }

  // Define the menuBar that is shown on the top of the window
  private val menuBar: MenuBar = new MenuBar() {

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
        }
        offFXAndWait {
          reader.load()
        }
        try {
          val points = reader.getDataPoints
          val invalidLines = reader.getFaultyLines
          if (invalidLines.length > 0) {
            Dialogs.showDialogWithExpandedText(AlertType.Warning,
              "File contained invalid data!",
              s"${invalidLines.length}/${invalidLines.length + points.length} of your lines had incorrect format",
              "Correct format is \"X;Y\" or \"Y;X\"",
              "Notice the semicolon separator! Examples of correct format:\n" +
                GlobalVars.correctFormatExamples + "\n\nYour faulty lines were:\n" + invalidLines.mkString("\n"))
          }
          println("Successfully loaded data points!")
          if (points.length > 4000) {
            Dialogs.showWarning("Data size warning!",
              "This might take some time to draw all the dots...",
              s"Your data contains ${points.length} points")
          }
          Plot.dataPoints = points
          updateAllPlots()
        } catch {
          case c: CustomDialogException =>
            Dialogs.showError(c.title, c.header, c.text)
          case e: ExpandedDialogException =>
            Dialogs.showDialogWithExpandedText(AlertType.Warning, e.title, e.header, e.text, e.expanded)
          case other: Throwable =>
            println(other.getMessage)
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
              val smallerSide = math.min(Plot.width(), Plot.height())
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
          new MenuItem("Swap X and Y") {
            onAction = _ =>
              if (Plot.dataPoints.length == 0) {
                Dialogs.showError("Empty data error", "You haven't read any data yet", "Please choose some datafile!")
              } else {
                Plot.dataPoints.foreach(_.reverseXY())
                updateAllPlots()
              }
          },
          new SeparatorMenuItem, // Use separators to keep this clean
          new MenuItem("Y axis title") {
            onAction = _ => {
              Plot.yAxis.label = Dialogs.showAxisTitleDialog(false)
            }
          },
          new MenuItem("X axis title") {
            onAction = _ => {
              Plot.xAxis.label = Dialogs.showAxisTitleDialog(true)
            }
          },
          new SeparatorMenuItem,
          // The point style options are under this separator
          new MenuItem("Point color") {
            onAction = _ => Dialogs.showColorMenu()
          },
          newMenuItem("Point shape", (GlobalVars.styleOptions.keys.toArray.sorted, styleToggle), "circle"),
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
          // Dark mode toggle that loads the correct .css file
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
            onAction = _ => Dialogs.showInfo("About", "This program was made by Matias Södersved",
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

