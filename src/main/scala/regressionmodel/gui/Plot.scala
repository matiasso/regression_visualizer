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
    // The last thing is to apply styles (We have to do this each time)
    this.pointSeries.applyStyles()
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
    val xyDuplicates = this.dataPoints.groupBy(_.first).exists(_._2.length > 1)
    val yxDuplicates = this.dataPoints.groupBy(_.second).exists(_._2.length > 1)
    (xyDuplicates, yxDuplicates)
  }


  def updateLimits(): Unit = {
    this.limitsHelper(PlotLimits.xMin, PlotLimits.xMax, this.xAxis, xAxisBool = true)
    this.limitsHelper(PlotLimits.yMin, PlotLimits.yMax, this.yAxis, xAxisBool = false)
  }

  private def limitsHelper(limA: Option[Double], limB: Option[Double], axis: NumberAxis, xAxisBool: Boolean): Unit = {
    def takeFirst(xyFormat: Boolean, xBool: Boolean): Boolean = {
      // "X;Y" format and xBool --> true
      // "X;Y" format and !xBool --> false
      // "Y;X" format and xBool --> false
      // "Y;X" format and !xBool --> true
      (xyFormat && xBool) || (!xyFormat && !xBool)
    }

    axis.autoRanging = false
    (limA, limB) match {
      case (Some(a), Some(b)) =>
        // If the user has specified certain limits, we'll use those!
        axis.lowerBound = a
        axis.upperBound = b
      case _ =>
        if (this.dataPoints.length > 1) {
          if (xAxisBool) {
            axis.lowerBound = if (takeFirst(GlobalVars.leftCoordinateIsX, xAxisBool))
              this.dataPoints.minBy(_.first).first else this.dataPoints.minBy(_.second).second
            axis.upperBound = if (takeFirst(GlobalVars.leftCoordinateIsX, xAxisBool))
              this.dataPoints.maxBy(_.first).first else this.dataPoints.maxBy(_.second).second
            axis.lowerBound = math.floor(axis.getLowerBound - math.abs(axis.getLowerBound) / 50)
            axis.upperBound = math.ceil(axis.getUpperBound + math.abs(axis.getUpperBound) / 50)
          } else {
            axis.autoRanging = true
          }
        } else if (this.dataPoints.length == 1) {
          // If theres exactly one point we want to "pad" around it
          val head = this.dataPoints.head
          axis.lowerBound = math.floor((if (takeFirst(GlobalVars.leftCoordinateIsX, xAxisBool))
            head.first else head.second) - 5)
          axis.upperBound = math.ceil((if (takeFirst(GlobalVars.leftCoordinateIsX, xAxisBool))
            head.first else head.second) + 5)
        } else {
          // If there's no points available we'll use default -10 and 10
          axis.lowerBound = -10
          axis.upperBound = 10
        }
    }
    this.setTickUnit(axis)
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
