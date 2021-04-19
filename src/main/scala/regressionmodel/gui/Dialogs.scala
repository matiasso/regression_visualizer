package regressionmodel.gui

import regressionmodel.GlobalVars
import regressionmodel.gui.dialogresults._
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control._
import scalafx.scene.layout._

object Dialogs {

  def showLimitDialog(xAxis: Boolean): Unit = {
    val dialog = new Dialog[LimitResult]() {
      initOwner(GlobalVars.myStage)
      title = "Plot limits for " + (if (xAxis) "X" else "Y") + "-axis"
      headerText = "Please input lower and upper bounds"
    }
    /*dialog.dialogPane().setMinWidth(280)
    dialog.dialogPane().setMinHeight(220)*/


    val okButtonType = new ButtonType("OK", ButtonData.OKDone)
    val clearButtonType = new ButtonType("Clear")

    dialog.dialogPane().getButtonTypes.addAll(okButtonType, clearButtonType, ButtonType.Cancel)

    val limA = new TextField() {
      promptText = if (xAxis) PlotLimits.xMin.getOrElse("-10").toString else PlotLimits.yMin.getOrElse("-10").toString
    }
    val limB = new TextField() {
      promptText = if (xAxis) PlotLimits.xMax.getOrElse("10").toString else PlotLimits.yMax.getOrElse("10").toString
    }
    val errorLabel = new Label("") {
      style = "-fx-text-fill: red"
    }
    val padGap = 10
    val rowGap = 6
    val grid = new VBox() {
      children = Seq(
        new HBox() {
          children = Seq(new Label("Lower bound:"), limA)
          spacing = padGap
        },
        new HBox() {
          children = Seq(new Label("Upper bound:"), limB)
          spacing = padGap
        },
        errorLabel
      )
      padding = Insets(padGap, padGap, padGap, padGap)
      spacing = rowGap
    }
    val okButton = dialog.dialogPane().lookupButton(okButtonType)
    okButton.setDisable(true)

    //Make use for this errorLabel later!

    //Add checks here for the double format, just as an extra feature :)
    def checkEnableOkButton(): Unit = {
      //Enable the button ONLY if both inputs are in Double format
      val doubleOptA = limA.text().toDoubleOption
      val doubleOptB = limB.text().toDoubleOption
      (doubleOptA, doubleOptB) match {
        case (Some(a), Some(b)) =>
          okButton.setDisable(a >= b) //if a >= b then disable the button
          errorLabel.text = if (a >= b) "ERROR: Lower bound bigger than upper bound" else ""
        case _ =>
          okButton.setDisable(true)
          if (limA.text().isEmpty || limB.text().isEmpty)
            errorLabel.text = "You need to enter both limits"
          else
            errorLabel.text = "Please enter two proper decimals"
      }
    }

    limA.text.onChange { (_, _, _) =>
      checkEnableOkButton()
    }
    limB.text.onChange { (_, _, _) =>
      checkEnableOkButton()
    }

    dialog.dialogPane().setContent(grid)
    dialog.resultConverter = dButton => {
      dButton.text match {
        case okButtonType.text => LimitResult(limA.text().toDoubleOption, limB.text().toDoubleOption)
        case clearButtonType.text => LimitResult(None, None)
        case _ => LimitResult(if (xAxis) PlotLimits.xMin else PlotLimits.yMin, if (xAxis) PlotLimits.xMax else PlotLimits.yMax)
      }
    }
    val result = dialog.showAndWait()
    result match {
      case Some(LimitResult(a, b)) =>
        //Send these values to the Plot graph!
        if (xAxis)
          PlotLimits.setLimitsX(a, b)
        else
          PlotLimits.setLimitsY(a, b)
      case _ => // This will never get called since we always return some LimitResult type!
    }
    Plot.updateLimits()
    Plot.updateRegressionSeries()
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
      case _ => println("Received no color")
    }
  }

  def showTxtCsvFormatMenu(): (Boolean, Boolean) = {
    val dialog = new Dialog[TxtCsvResult]() {
      initOwner(GlobalVars.myStage)
      title = "Reader options"
      headerText = "Choose which applies to your data"
    }

    val okButtonType = new ButtonType("OK", ButtonData.OKDone)
    dialog.dialogPane().getButtonTypes.add(okButtonType)

    // Create two ChoiceBoxes for Format X;Y / Y;X and to check unique X!
    val formatChoiceBox = new ChoiceBox(ObservableBuffer("X;Y", "Y;X")) {
      value = "X;Y"
    }
    val uniqueChoiceBox = new ChoiceBox(ObservableBuffer("ON", "OFF")) {
      value = "OFF"
    }
    val padAndGap = 10
    val grid = new GridPane() {
      hgap = padAndGap
      vgap = padAndGap
      padding = Insets(padAndGap, padAndGap, padAndGap, padAndGap)
      addRow(0, new Label("Format:\t"), formatChoiceBox)
      addRow(1, new Label("Unique X:\t"), uniqueChoiceBox)
    }
    dialog.dialogPane().setContent(grid)
    dialog.resultConverter = dButton =>
      if (dButton == okButtonType) {
        TxtCsvResult(formatChoiceBox.getValue == "X;Y", uniqueChoiceBox.getValue == "ON")
      }
      else
        null

    val result = dialog.showAndWait()
    result match {
      case Some(TxtCsvResult(leftX, unique)) =>
        (leftX, unique)
      case _ => println("Null returned in TxtCsvDialog")
        (true, false) // This is the default case we want to return
    }
  }

  def showSizeDialog(): Unit = {
    val SmallButton = new ButtonType("Small")
    val MediumButton = new ButtonType("Medium")
    val LargeButton = new ButtonType("Large")
    val XXLButton = new ButtonType("Extra Large")

    val alert = new Alert(AlertType.Confirmation) {
      initOwner(GlobalVars.myStage)
      title = "Symbol size dialog"
      headerText = "Custom size selector"
      contentText = "Choose the size of the symbols"
      buttonTypes = Seq(SmallButton, MediumButton, LargeButton, XXLButton, ButtonType.Cancel)
    }
    val result = alert.showAndWait()
    val size = result match {
      case Some(SmallButton) => 0
      case Some(MediumButton) => 2
      case Some(LargeButton) => 4
      case Some(XXLButton) => 6
      case _ => -1
    }
    if (size >= 0)
      Plot.pointSeries.setSize(size)
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

  def showDialogWithExpandedText(alertType: AlertType, titleStr: String, header: String, content: String, expandedStr: String): Option[ButtonType] = {
    val textArea = new TextArea {
      text = expandedStr
      editable = false
      wrapText = true
      maxWidth = Double.MaxValue
      maxHeight = Double.MaxValue
      vgrow = Priority.Always
      hgrow = Priority.Always
    }
    val expandingNode = new StackPane() {
      children = textArea
    }
    this.showDialogWithExpandableContent(alertType, titleStr, header, content, expandingNode)
  }

  private def showDialogWithExpandableContent(alertType: AlertType, titleStr: String, header: String, content: String, exp: Node): Option[ButtonType] = {
    new Alert(alertType) {
      initOwner(GlobalVars.myStage)
      title = titleStr
      headerText = header
      contentText = content
      dialogPane().setExpandableContent(exp)
    }.showAndWait()
  }

}
