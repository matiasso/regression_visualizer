package regressionmodel.gui

import regressionmodel.{GlobalVars, Main}
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, Label, ProgressBar}
import scalafx.scene.layout.GridPane

object BottomPanel extends GridPane {
  val paddingInt = 10
  val labelRSquared = new Label(GlobalVars.labelUnknownText)
  val labelFunc = new Label(GlobalVars.labelUnknownText)
  val progressBar: ProgressBar = new ProgressBar() {
    progress = 0
  }
  val debugButton: Button = new Button("Print points!") {
    onAction = _ => {
      //Plot.setPointStyle("-fx-background-color: green, white;")
      Plot.dataPoints.foreach(println)
    }
  }

  this.padding = Insets(paddingInt, paddingInt, paddingInt, paddingInt)
  this.hgap = paddingInt

  this.addRow(0, new Label("R squared: "), labelRSquared, debugButton)
  this.addRow(1, new Label("Graph f(x): "), labelFunc, progressBar)

  def updateFunctionLabel(tuple: (Option[Double], Option[Double]), linear: Boolean): Unit = {
    //Check whether we're using linear or exponential graph
    tuple match {
      case (Some(m), Some(b)) =>
        val bTex = if (b >= 0) s"+$b" else b
        if (linear) {
          this.labelFunc.text = s"y=$m*x$bTex"
        } else {
          this.labelFunc.text = s"y=$b*e${this.expPowerToSuperscript(m + "x")}"
        }
      case _ => this.labelFunc.text = GlobalVars.labelUnknownText
    }
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
      case 'x' => '\u02D9' // ˣ
      case _ => ' '
    }
  }

  private def expPowerToSuperscript(exp: String): String = {
    exp.foldLeft("")((prev, cur) => prev + this.getSuperscript(cur))
  }

  def updateRSquared(rSquared: Option[Double]): Unit = {
    rSquared match {
      case Some(r) => this.labelRSquared.text = r.toString
      case None => this.labelRSquared.text = GlobalVars.labelUnknownText
    }
  }
}
