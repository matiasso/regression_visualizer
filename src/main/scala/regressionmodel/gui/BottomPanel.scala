package regressionmodel.gui

import javafx.scene.layout.ColumnConstraints
import regressionmodel.GlobalVars
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, Label, ProgressBar}
import scalafx.scene.layout.{GridPane, Priority}

object BottomPanel extends GridPane {
  val paddingInt = 10
  val labelRSquared = new Label("R² value:")
  val labelFunc = new Label("Graph f(x): ")
  val progressBar: ProgressBar = new ProgressBar() {
    hgrow = Priority.Always
    maxWidth = 600
    minWidth = 150
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

  this.addRow(0, labelRSquared)
  this.addRow(1, labelFunc, progressBar)

  val cc = new ColumnConstraints()
  cc.setPercentWidth(50)
  this.getColumnConstraints.add(cc)

  def updateFunctionLabel(tuple: (Option[Double], Option[Double]), linear: Boolean): Unit = {
    //Check whether we're using linear or exponential graph
    this.labelFunc.text = "Graph f(x):\t" + (tuple match {
      case (Some(m), Some(b)) =>
        val bWithSign = if (b >= 0) s"+$b" else b
        if (linear) {
          s"y=$m*x$bWithSign"
        } else {
          s"y=$b*e${this.expPowerToSuperscript(m + "x")}"
        }
      case _ => GlobalVars.labelUnknownText
    })
  }

  def updateRSquared(rSquared: Option[Double]): Unit = {
    this.labelRSquared.text = "R² value:\t" + (rSquared match {
      case Some(r) => r.toString
      case None => GlobalVars.labelUnknownText
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
