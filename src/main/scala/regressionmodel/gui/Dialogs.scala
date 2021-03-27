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
