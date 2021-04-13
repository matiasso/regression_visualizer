package regressionmodel.gui

import org.scalafx.extras.BusyWorker
import regressionmodel.GlobalVars
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, Label, ProgressBar}
import scalafx.scene.layout.{HBox, Priority, VBox}

object BottomPanel extends VBox {

  val paddingInt = 10
  val labelRSquared = new Label(GlobalVars.textRSquared)
  val labelFunc = new Label(GlobalVars.textForGraphLabel)
  val progressBar: ProgressBar = new ProgressBar() {
    progress = 0
  }
  val progressLabel: Label = new Label()
  var busyworker: BusyWorker = _  // Define this later in DataPointSeries.update()
  // Add "Copy graph string" button
  val buttonLess: Button = new Button("←") {
    onAction = _ => {

    }
  }
  val buttonMore: Button = new Button("→") {
    onAction = _ => {
    }
  }
  val secondHBox: HBox = new HBox() {
    spacing = paddingInt
    children = Seq(buttonLess, new Label("Decimal amount"), buttonMore, progressLabel)
  }
  for (node <- Seq(labelRSquared, labelFunc, progressBar, secondHBox)) {
    node.hgrow = Priority.Always
    node.maxWidth = Double.MaxValue
  }

  this.padding = Insets(paddingInt)
  this.spacing = paddingInt
  this.children = Seq(new HBox() {
    children = Seq(labelRSquared, secondHBox)
    spacing = paddingInt
  }, new HBox() {
    children = Seq(labelFunc, progressBar)
    spacing = paddingInt
  })

  def updateFunctionLabel(tuple: (Option[Double], Option[Double]), linear: Boolean): Unit = {
    //Check whether we're using linear or exponential graph
    this.labelFunc.text = GlobalVars.textForGraphLabel + "\t" + (tuple match {
      case (Some(m), Some(b)) =>
        val bWithSign = if (b >= 0) s"+$b" else b
        if (linear) {
          s"y=$m*x$bWithSign"
        } else {
          s"y=$b*e${this.expPowerToSuperscript(m + "x")}"
        }
      case _ => GlobalVars.textUnknownCoefficients
    })
  }

  def updateRSquared(rSquared: Option[Double]): Unit = {
    this.labelRSquared.text = GlobalVars.textRSquared + "\t" + (rSquared match {
      case Some(r) => r.toString
      case None => GlobalVars.textUnknownCoefficients
    })
  }

  private def getSuperscript(char: Char): Char = {
    char match {
      case '0' => '\u2070' // ⁰
      case '1' => '\u00B9' // ¹
      case '2' => '\u00B2' // ²
      case '3' => '\u00B3' // ³
      case '4' => '\u2074' // ⁴
      case '5' => '\u2075' // ⁵
      case '6' => '\u2076' // ⁶
      case '7' => '\u2077' // ⁷
      case '8' => '\u2078' // ⁸
      case '9' => '\u2079' // ⁹
      case '.' => '\u02D9' // ˙
      case 'x' => '\u02E3' // ˣ
      case _ => ' '
    }
  }

  private def expPowerToSuperscript(exp: String): String = {
    exp.foldLeft("")((prev, cur) => prev + this.getSuperscript(cur))
  }
}
