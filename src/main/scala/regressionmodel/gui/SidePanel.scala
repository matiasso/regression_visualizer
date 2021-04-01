package regressionmodel.gui

import scalafx.geometry.Insets
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.GridPane

object SidePanel extends GridPane {
  val paddingInt = 10
  val labelRSquared = new Label("???")
  val labelFunc = new Label("???")
  val debugButton: Button = new Button("Print points!") {
    onAction = _ => {
      //Plot.setPointStyle("-fx-background-color: green, white;")
      Plot.dataPoints.foreach(println)
    }
  }

  this.padding = Insets(paddingInt, paddingInt, paddingInt, paddingInt)
  this.hgap = paddingInt

  this.addRow(0, new Label("R squared:"), labelRSquared)
  this.addRow(1, new Label("Graph f(x): "), labelFunc, debugButton)

  def updateFunctionLabel(tuple: (Option[Double], Option[Double]), linear: Boolean): Unit = {
    //Check whether we're using linear or exponential graph
    tuple match {
      case (Some(m), Some(b)) =>
        val bTex = if (b >= 0) s"+$b" else b
        if (linear) {
          this.labelFunc.text = s"y=$m*x$bTex"
        } else {
          this.labelFunc.text = s"y=$b*e^($m*x)"
        }
      case _ => this.labelFunc.text = "???"
    }
  }

  def updateRSquared(rSquared: Option[Double]): Unit = {
    rSquared match {
      case Some(r) => this.labelRSquared.text = r.toString
      case None => this.labelRSquared.text = "???"
    }
  }
}
