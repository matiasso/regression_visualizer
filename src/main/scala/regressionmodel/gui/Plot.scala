package regressionmodel.gui

import regressionmodel.mathematics.{ExponentialRegression, LinearRegression}
import regressionmodel.{GlobalVars, PVector}
import scalafx.scene.chart.{NumberAxis, ScatterChart}
import scalafx.scene.layout.StackPane

object Plot extends StackPane {

  var dataPoints: Array[PVector] = new Array[PVector](0)

  //Define both x and y axis
  val xAxis: NumberAxis = new NumberAxis(-10, 10, 1) {
    label = "X-axis"
    minorTickCount = 5
  }
  val yAxis: NumberAxis = new NumberAxis(-10, 10, 1) {
    label = "Y-axis"
    minorTickCount = 5
  }
  val pointSeries: DataPointSeries = new DataPointSeries("Points")
  val regressionSeries: RegressionSeries = new RegressionSeries("Regression")
  val scatterChart: ScatterChart[Number, Number] = new ScatterChart[Number, Number](this.xAxis, this.yAxis) {
    title = "Regression model by Matias"
    animated = false
  }
  scatterChart.getData.addAll(pointSeries.series, regressionSeries.series)
  this.children = scatterChart


  def update(): Unit = {
    if (this.dataPoints.length == 0) {
      // If it's empty, we can simply remove our data
      this.clearPlot()
    } else {
      // Optimize this later
      this.pointSeries.update()
    }
  }

  def updateRegressionSeries(): Unit = {
    if (this.dataPoints.length > 0) {
      this.regressionSeries.update()
    }
  }

  def clearPlot(): Unit = {
    this.dataPoints = new Array[PVector](0)
    this.pointSeries.clear()
    this.regressionSeries.clear()
    // Since LinearRegression and ExponentialRegression are both objects we can clear their values this way
    LinearRegression.clearAll()
    ExponentialRegression.clearAll()
    BottomPanel.updateRSquared()
    BottomPanel.updateFunctionLabel()
    // If the user hasn't specified any limits, we'll make both axis in range [-10, 10]
    this.updateLimits()
  }


  //This should be checked everytime we change XY / YX format and when data changes
  def checkForDuplicates: (Boolean, Boolean) = {
    val xyDuplicates = this.dataPoints.groupBy(_.x).exists(_._2.length > 1)
    val yxDuplicates = this.dataPoints.groupBy(_.y).exists(_._2.length > 1)
    (xyDuplicates, yxDuplicates)
  }


  def updateLimits(): Unit = {
    this.limitsHelper(PlotLimits.xMin, PlotLimits.xMax, this.xAxis, xAxisBool = true)
    this.limitsHelper(PlotLimits.yMin, PlotLimits.yMax, this.yAxis, xAxisBool = false)
  }

  private def limitsHelper(limA: Option[Double], limB: Option[Double], axis: NumberAxis, xAxisBool: Boolean): Unit = {
    axis.autoRanging = false
    (limA, limB) match {
      case (Some(a), Some(b)) =>
        // If the user has specified certain limits, we'll use those!
        axis.lowerBound = a
        axis.upperBound = b
      case _ =>
        if (this.dataPoints.length > 0) {
          val axisValues = if (xAxisBool) this.dataPoints.groupBy(_.x) else this.dataPoints.groupBy(_.y)
          // Check whether there are DIFFERENT values on this axis
          if (axisValues.size > 1) {
            if (xAxisBool && PlotLimits.yMin.isEmpty && PlotLimits.yMax.isEmpty) {
              axis.lowerBound = dataPoints.minBy(_.x).x
              axis.upperBound = dataPoints.maxBy(_.x).x
              val axisLength = axis.getUpperBound - axis.getLowerBound
              axis.lowerBound = math.floor(axis.getLowerBound - axisLength / 50)
              axis.upperBound = math.ceil(axis.getUpperBound + axisLength / 50)
            } else {
              // Y Axis may be autoranging
              axis.autoRanging = true
            }
          } else {
            // We'll pad +5 and -5 around this axis since all values are equal
            val head = this.dataPoints.head
            axis.lowerBound = math.floor((if(xAxisBool) head.x else head.y) - 5)
            axis.upperBound = math.ceil((if (xAxisBool) head.x else head.y) + 5)
          }
        } else {
          // If there's no points available we'll use default -10 and 10
          axis.lowerBound = -10
          axis.upperBound = 10
        }
    }
    setTickUnit(axis)
  }

  def setTickUnit(axis: NumberAxis): Unit = {
    val diff = axis.getUpperBound - axis.getLowerBound
    if (diff > 20) {
      axis.tickUnit = math.round(diff / 20)
    } else if (diff >= 7) {
      axis.tickUnit = 1
    } else if (diff >= 2) {
      axis.tickUnit = 0.25
    } else {
      // This will sometimes show weird values
      axis.tickUnit = diff / 20
    }
  }
}
