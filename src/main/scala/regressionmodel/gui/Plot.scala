package regressionmodel.gui

import regressionmodel.GlobalVars
import scalafx.scene.chart.{ScatterChart, XYChart}
import scalafx.scene.layout.StackPane

class Plot extends StackPane {

  val sc: ScatterChart[Number, Number] =  ScatterChart[Number, Number](GlobalVars.xAxis, GlobalVars.yAxis)
  sc.setTitle("Regression model by Matias")
  val series: XYChart.Series[Number, Number] = new XYChart.Series()
  series.getData.add(XYChart.Data(-4, 6))
  series.getData.add(XYChart.Data(-3, 4))
  series.getData.add(XYChart.Data(1, 5.5))
  series.getData.add(XYChart.Data(2, 7))
  series.getData.add(XYChart.Data(3, 8.124))
  series.getData.add(XYChart.Data(7.24, 1.23))
  series.getData.add(XYChart.Data(8.857, 4.12))

  sc.getData.addAll(series)
  this.children = sc
}
