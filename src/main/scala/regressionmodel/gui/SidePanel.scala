package regressionmodel.gui

import scalafx.geometry.Insets
import scalafx.scene.control.Label
import scalafx.scene.layout.GridPane

object SidePanel extends GridPane {
  val paddingInt = 10
  val labelRSquared = new Label("???")
  val labelFunc = new Label("???")

  this.padding = Insets(paddingInt, paddingInt, paddingInt, paddingInt)
  this.hgap = paddingInt

  this.addRow(0, new Label("R squared:"), labelRSquared)
  this.addRow(1, new Label("Graph f(x): "), labelFunc)

  def updateFunctionLabel(tuple: (Double, Double), linear: Boolean): Unit = {
    //Check whether we're using linear or exponential graph
    val b = if (tuple._2 >= 0) s"+${tuple._2}" else tuple._2
    if (linear){
      this.labelFunc.text = s"y=${tuple._1}x$b"
    } else {
      this.labelFunc.text = s"y=${tuple._1}^x+$b"
    }
  }
}
