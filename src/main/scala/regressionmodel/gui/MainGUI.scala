package regressionmodel.gui

import org.scalafx.extras.{offFXAndWait, onFX}
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

import javax.imageio.ImageIO

class MainGUI extends BorderPane {


  val regressionTypeToggle = new ToggleGroup
  regressionTypeToggle.selectedToggle.onChange((_, oldVal, newVal) => {
    //If oldVal is null, it's the first (instantion) selection and theres no need to update anything
    if (oldVal != null) {
      Plot.regressionSeries.regressionObject = regressionTypeToggle.getSelectedToggle.asInstanceOf[javafx.scene.control.RadioMenuItem].getText.toLowerCase match {
        case "exponential" => ExponentialRegression
        case _ => LinearRegression
      }
      Plot.updateRegressionSeries()
    }
  })

  val dataFormatToggle = new ToggleGroup
  dataFormatToggle.selectedToggle.onChange((_, oldVal, newVal) => {
    //If oldVal is null, it's the first (instantion) selection and theres no need to update anything
    if (oldVal != null) {
      //This should return to the old value IF there is duplicate error!
      GlobalVars.leftCoordinateIsX = dataFormatToggle.getSelectedToggle.asInstanceOf[javafx.scene.control.RadioMenuItem].getText.toLowerCase match {
        case "x;y" => true
        //case "y;x" => false
        case _ => false
      }
      Plot.update()
      Plot.updateLimits()
      Plot.updateRegressionSeries()
    }
  })
  val styleToggle = new ToggleGroup
  styleToggle.selectedToggle.onChange({
    val key = styleToggle.getSelectedToggle.asInstanceOf[javafx.scene.control.RadioMenuItem].getText.toLowerCase
    if (GlobalVars.styleOptions.contains(key)) {
      Plot.pointSeries.setStyle(GlobalVars.styleOptions(key))
    } else {
      println("A weird error occurred in styleToggle.onChange")
    }
  })

  def newMenuItem(text: String, tuple: (Array[String], ToggleGroup)): Menu = {
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
          val points = reader.getDataPoints
          println("Successfully loaded data points!")
          onFX {
            //Optimize these later with a busy worker
            Plot.dataPoints.clear()
            Plot.dataPoints.addAll(points)
            Plot.updateLimits()
            Plot.updateRegressionSeries()
          }
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
              ImageIO.write(bufferedImage, "png", filePath)
              println("Image written successfully!")
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
      Plot.dataPoints.clear()
    }
    val exit = new MenuItem("Exit")
    exit.onAction = _ => sys.exit(0)
    menus = List(
      new Menu("File") {
        items = List(open, save, close, exit)
      },
      new Menu("Settings") {
        items = List(
          newMenuItem("Regression type", (GlobalVars.regressionOptions, regressionTypeToggle)),
          newMenuItem("Data format", (GlobalVars.dataFormatOptions, dataFormatToggle)),
          new MenuItem("Point color") {
            onAction = _ => Dialogs.showColorMenu()
          },
          newMenuItem("Point style", (GlobalVars.styleOptions.keys.toArray, styleToggle)),
          new MenuItem("X-axis limits") {
            onAction = _ => Dialogs.showLimitDialog(true)
          },
          new MenuItem("Y-axis limits") {
            onAction = _ => Dialogs.showLimitDialog(false)
          }
        )
      },

      new Menu("Help") {
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

