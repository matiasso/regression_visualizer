package regressionmodel.gui

import org.scalafx.extras.offFXAndWait
import regressionmodel.GlobalVars
import scalafx.scene.chart.XYChart

class DataPointSeries(name: String) extends PointSeries(name) {

  val index = 0

  override def update(): Unit = {
    this.series.getData.clear()
    var i = 0
    for (point <- Plot.dataPoints) {
      if (GlobalVars.leftCoordinateIsX) {
        this.series.getData.add(XYChart.Data(point.first, point.second))
      } else {
        this.series.getData.add(XYChart.Data(point.second, point.first))
      }
      // The window freezes with big datafiles so I'd like to slow this down and show progress instead
      i += 1
      BottomPanel.progressBar.progress = i * 1.0 / Plot.dataPoints.length
    }
  }
}
