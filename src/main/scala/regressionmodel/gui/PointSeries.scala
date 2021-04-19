package regressionmodel.gui

import scalafx.scene.chart.XYChart

abstract class PointSeries(val name: String) {

  var style: String = ""
  var size: String = ""
  var colorStyle: String = ""
  val index: Int
  val series: XYChart.Series[Number, Number] = new XYChart.Series()
  series.getData.add(XYChart.Data(0, 0))
  series.setName(name)

  def update()

  def clear(): Unit = this.series.getData.clear()

  def applyStyles(): Unit = {
    if (this.style.nonEmpty || this.colorStyle.nonEmpty) {
      Plot.lineChart.lookupAll(s".series$index").foreach(_.setStyle(this.styleString))
    }
  }

  def styleString: String = this.style + this.colorStyle + this.size

  def setStyle(styleStr: String): Unit = {
    this.style = styleStr
    this.applyStyles()
  }

  def setColor(styleStr: String): Unit = {
    this.colorStyle = styleStr
    this.applyStyles()
  }

  def setSize(size: Int): Unit = {
    // For some reason the insets have to be negative size, otherwice it mirrors my symbols upside down
    this.size = "-fx-background-insets: " + -size + "px;"
    this.applyStyles()
  }

}
