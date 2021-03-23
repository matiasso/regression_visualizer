package regressionmodel.gui

import regressionmodel.PVector
import regressionmodel.mathematics.{LinearRegression, RegressionModel}
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Side
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
  var leftCoordinateIsX = true
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
  scatterChart.legendSide = Side.Right
  this.children = scatterChart


  def updateData(): Unit = {
    if (this.dataPoints.length > 0) {
      pointSeries.getData.clear()
      //Clear and add all new data to the series
      for (p <- this.dataPoints) {
        if (leftCoordinateIsX){
          pointSeries.getData.add(XYChart.Data(p.x, p.y))
        } else{
          pointSeries.getData.add(XYChart.Data(p.y, p.x))
        }
      }
      regrObject.calculateCoefficients(leftCoordinateIsX)
      val coef: (Option[Double], Option[Double]) = regrObject.getCoefficients
      SidePanel.updateFunctionLabel(coef, this.isLinear)
      regrSeries.getData.clear()
      coef match {
        case (Some(m), Some(b)) =>
          //Clear and add the dots for the regressionModel
          for (x <- -100 to 100) {
            //This only works for the linear model!
            if (this.isLinear){
              regrSeries.getData.add(XYChart.Data(x / 10.0, m * (x / 10.0) + b))
            } else {
              regrSeries.getData.add(XYChart.Data(x / 10.0, b*math.exp(m * (x / 10.0))))
            }
          }
        case _ => println("Error in getCoefficients!")
      }
    }
  }

  def isLinear: Boolean = this.regrObject == LinearRegression

  def addPoint(point: PVector): Unit = {
    this.dataPoints.add(point)
  }
}
