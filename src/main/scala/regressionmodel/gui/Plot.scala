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
  var limits: (Option[Double], Option[Double]) = (None, None)
  //Define both x and y axis
  val xAxis = new NumberAxis(-10, 10, 1)
  xAxis.setLabel("X")
  xAxis.autoRanging = true
  val yAxis = new NumberAxis(-10, 10, 1)
  yAxis.setLabel("Y")
  yAxis.autoRanging = true
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
      this.updateRegressionLine()
    }
  }

  private def isLinear: Boolean = this.regrObject == LinearRegression

  private def updateRegressionLine(): Unit = {
    regrObject.calculateCoefficients(leftCoordinateIsX)
    val coef: (Option[Double], Option[Double]) = regrObject.getCoefficients
    SidePanel.updateFunctionLabel(coef, this.isLinear)
    regrSeries.getData.clear()
    coef match {
      case (Some(m), Some(b)) =>
        //Clear and add the dots for the regressionModel
        val points = dataPoints.map(p => if (leftCoordinateIsX) p.x else p.y)
        var start: Double = points.min
        var end: Double = points.max
        this.limits match {
          case (Some(a), Some(b)) =>
            if (a > start)
              start = a
            if (b < end)
              end = b
          case _ =>
        }
        val width = end - start
        val iterations = 300
        for (i <- 0 to iterations) {
          val x = start + i * width / iterations
          if (this.isLinear){
            regrSeries.getData.add(XYChart.Data(x, m * x + b))
          } else {
            regrSeries.getData.add(XYChart.Data(x, b*math.exp(m * x)))
          }
        }
      case _ => println("Error in getCoefficients!")
    }
  }

  def setLimits(limA: Option[Double], limB: Option[Double]): Unit = {
    this.limits = (limA, limB)
    (limA, limB) match {
      case (Some(a), Some(b)) =>
        this.xAxis.autoRanging = false
        this.xAxis.lowerBound = a
        this.xAxis.upperBound = b
      case _ => this.xAxis.autoRanging = true
    }
    this.updateRegressionLine()
  }

  def addPoint(point: PVector): Unit = {
    this.dataPoints.add(point)
  }
}
