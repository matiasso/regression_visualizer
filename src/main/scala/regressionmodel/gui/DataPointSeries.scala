package regressionmodel.gui

import regressionmodel.GlobalVars
import scalafx.scene.chart.XYChart

class DataPointSeries(name: String) extends PointSeries(name) {

  val index = 0

  override def update(): Unit = {
    this.series.getData.clear()
    for (point <- Plot.dataPoints) {
      if (GlobalVars.leftCoordinateIsX) {
        this.series.getData.add(XYChart.Data(point.first, point.second))
      } else {
        this.series.getData.add(XYChart.Data(point.second, point.first))
      }
    }
  }
}
