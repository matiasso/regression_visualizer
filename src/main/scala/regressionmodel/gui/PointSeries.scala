package regressionmodel.gui

import scalafx.scene.chart.XYChart

abstract class PointSeries(val name: String) {

  private var style: String = ""
  private var size: String = ""
  private var colorStyle: String = ""
  protected val index: Int
  val series: XYChart.Series[Number, Number] = new XYChart.Series()
  series.getData.add(XYChart.Data(0, 0)) // We have to add one datapoint there so that the legend works properly.
  series.setName(name)

  def update()

  def clear(): Unit = this.series.getData.clear()

  def applyStyles(): Unit = {
    if (this.style.nonEmpty || this.colorStyle.nonEmpty || this.size.nonEmpty) {
      Plot.lineChart.lookupAll(s".series$index").foreach(_.setStyle(this.styleString))
    }
  }

  private def styleString: String = this.style + this.colorStyle + this.size

  def setStyle(styleStr: String): Unit = {
    this.style = styleStr
    this.applyStyles()
  }

  def setColor(styleStr: String): Unit = {
    this.colorStyle = styleStr
    this.applyStyles()
  }

  def setSize(size: Int): Unit = {
    // I figured that I can set the size by changing insets. But positive values mirror the shapes upside down
    // For some reason the insets have to be negative size to keep symbols correct way up
    this.size = "-fx-background-insets: " + -size + "px;"
    this.applyStyles()
  }

}
