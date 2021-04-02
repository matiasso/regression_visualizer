package regressionmodel.gui

import regressionmodel.GlobalVars
import scalafx.geometry.Insets
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control.{Alert, ButtonType, Dialog, Label, TextField}
import scalafx.scene.layout.GridPane

object Dialogs {

  def showLimitDialog(xDialog: Boolean): Unit = {
    val dialog = new Dialog[LimitResult]() {
      initOwner(GlobalVars.myStage)
      title = "Plot limits for " + (if (xDialog) "X" else "Y") + "-axis"
      headerText = "Please input lower and upper bounds"
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
    val errorLabel = new Label("") {
      style = "-fx-text-fill: red"
    }
    //Make use for this errorLabel later!

    //Add checks here for the double format, just as an extra feature :)
    def checkEnableOkButton(): Unit = {
      //Enable the button ONLY if both inputs are in Double format
      val doubleOptA = limA.text().toDoubleOption
      val doubleOptB = limB.text().toDoubleOption
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

  def showColorMenu(): Unit = {
    val dialog = new Dialog[ColorResult]() {
      initOwner(GlobalVars.myStage)
      title = "Custom color for coordinate points"
      headerText = "Give the color in hexadecimal format"
    }

    val okButtonType = new ButtonType("OK", ButtonData.OKDone)
    dialog.dialogPane().getButtonTypes.add(okButtonType)

    val textField = new TextField() {
      //This promptText doesn't usually show because it's focused right away
      promptText = "Example: #FF33CC"
    }
    val padAndGap = 10
    val grid = new GridPane() {
      hgap = padAndGap
      vgap = padAndGap
      padding = Insets(padAndGap, padAndGap, padAndGap, padAndGap)
      addRow(0, new Label("Example: \"#FF33CC\""))
      addRow(1, new Label("Color code: "), textField)
    }
    val okButton = dialog.dialogPane().lookupButton(okButtonType)
    okButton.setDisable(true)

    //Add checks here for the hexadecimal format
    def checkEnableOkButton(): Unit = {
      okButton.setDisable(textField.text().replace("#", "").length != 6)
    }

    textField.text.onChange { (_, old, cur) =>
      //Restrict to only Hexadecimals A-F, 0-9
      val rgx = """^#?[0-9a-fA-F]*$""".r
      if (rgx.findFirstIn(cur).isEmpty || cur.length > 7) {
        textField.text = old
      } else {
        checkEnableOkButton()
      }
    }
    dialog.dialogPane().setContent(grid)
    dialog.resultConverter = dButton =>
      if (dButton == okButtonType)
        ColorResult(textField.text().replace("#", ""))
      else
        null
    val result = dialog.showAndWait()
    result match {
      case Some(ColorResult(clr)) =>
        println(s"User gave color code $clr")
        Plot.pointSeries.setColor(s"-fx-background-color: #$clr;")
        //Send this color code to the PointSeries
      case None => println("Received no color")
    }
  }

  def showWarning(titleStr: String, header: String, content: String): Option[ButtonType] = {
    this.showBasicDialog(AlertType.Warning, titleStr, header, content)
  }

  def showInfo(titleStr: String, header: String, content: String): Option[ButtonType] = {
    this.showBasicDialog(AlertType.Information, titleStr, header, content)
  }

  def showError(titleStr: String, header: String, content: String): Option[ButtonType] = {
    this.showBasicDialog(AlertType.Error, titleStr, header, content)
  }

  private def showBasicDialog(alertType: AlertType, titleStr: String, header: String, content: String): Option[ButtonType] = {
    new Alert(alertType) {
      initOwner(GlobalVars.myStage)
      title = titleStr
      headerText = header
      contentText = content
    }.showAndWait()
  }

}
