package regressionmodel.gui

import regressionmodel.PVector
import regressionmodel.mathematics.{LinearRegression, RegressionModel}
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{NumberAxis, ScatterChart, XYChart}
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.Purple

object Plot extends StackPane {

  var regrObject: RegressionModel = LinearRegression
  val dataPoints: ObservableBuffer[PVector] = ObservableBuffer[PVector]()
  dataPoints.onChange {
    updateData()
  }
  var pointRadius = 1.5
  var graphColor: Color = Purple
  val xAxis = new NumberAxis(-10, 10, 1)
  xAxis.setLabel("X")
  val yAxis = new NumberAxis(-10, 10, 1)
  yAxis.setLabel("Y")
  val pointSeries: XYChart.Series[Number, Number] = new XYChart.Series()
  pointSeries.setName("Points")
  val regrSeries: XYChart.Series[Number, Number] = new XYChart.Series()
  regrSeries.setName("Regression")
  val scatterChart: ScatterChart[Number, Number] = ScatterChart[Number, Number](this.xAxis, this.yAxis)
  scatterChart.setTitle("Regression model by Matias")
  scatterChart.getData.addAll(pointSeries, regrSeries)
  this.children = scatterChart


  def updateData(): Unit = {
    if (this.dataPoints.length > 0) {
      pointSeries.getData.clear()
      //Clear and add all new data to the series
      for (p <- this.dataPoints) {
        pointSeries.getData.add(XYChart.Data(p.x, p.y))
      }
      regrObject.calculateCoefficients()
      val coef: (Double, Double) = regrObject.getCoefficients
      println("Coefficients: " + coef._1 + ", " + coef._2)
      regrSeries.getData.clear()
      //Clear and add the dots for the regressionModel
      for (x <- -100 to 100) {
        //This only works for the linear model
        regrSeries.getData.add(XYChart.Data(x / 10.0, coef._1 * (x / 10.0) + coef._2))
      }
    }
  }

  def addPoint(point: PVector): Unit = {
    this.dataPoints.add(point)
  }
}
