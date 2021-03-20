package regressionmodel.gui

import regressionmodel.mathematics.RegressionModel
import regressionmodel.PVector
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{NumberAxis, ScatterChart, XYChart}
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.Purple

object Plot extends StackPane {

  val dataPoints:ObservableBuffer[PVector] = ObservableBuffer[PVector]()
  dataPoints.onChange { updateData() }
  var pointRadius = 1.5
  var graphColor:Color = Purple
  val xAxis = new NumberAxis(-10, 10, 1)
  xAxis.setLabel("X")
  val yAxis = new NumberAxis(-10, 10, 1)
  yAxis.setLabel("Y")
  val pointSeries: XYChart.Series[Number, Number] = new XYChart.Series()
  pointSeries.setName("Points")
  val regrSeries: XYChart.Series[Number, Number] = new XYChart.Series()
  regrSeries.setName("Regression")
  val sc: ScatterChart[Number, Number] =  ScatterChart[Number, Number](this.xAxis, this.yAxis)
  sc.setTitle("Regression model by Matias")
  this.children = sc


  def updateData() : Unit = {
    val series: XYChart.Series[Number, Number] = this.pointSeries
    series.getData.clear()
    for (p <- this.dataPoints){
      series.getData.add(XYChart.Data(p.x, p.y))
    }
    val regrInstance = new RegressionModel(this.dataPoints.toArray)
    regrInstance.calculateCoefficients()
    val coef:(Double, Double) = regrInstance.getCoefficients
    println("Coefficients: " + coef._1 + ", " + coef._2)
    regrSeries.getData.clear()
    for (x <- -100 to 100){
      regrSeries.getData.add(XYChart.Data(x/10.0, coef._1*(x/10.0)+coef._2))
    }
    sc.getData.clear()
    sc.getData.addAll(series, regrSeries)
  }

  def addPoint(point: PVector) : Unit = {
     this.dataPoints.add(point)
  }
}
