package regressionmodel.gui

import scalafx.scene.chart.XYChart

abstract class PointSeries(val name: String) {

  var style: String = ""
  var colorStyle: String = ""
  val index: Int
  val series: XYChart.Series[Number, Number] = new XYChart.Series()
  series.getData.add(XYChart.Data(0, 0))
  series.setName(name)

  def update()

  def applyStyles(): Unit = {
    if (this.style.nonEmpty || this.colorStyle.nonEmpty) {
      Plot.scatterChart.lookupAll(s".series$index").forEach(_.setStyle(this.style + this.colorStyle))
    }
  }

  def setStyle(styleStr: String): Unit = {
    this.style = styleStr
    this.applyStyles()
  }

  def setColor(styleStr: String): Unit = {
    this.colorStyle = styleStr
    this.applyStyles()
  }

}
