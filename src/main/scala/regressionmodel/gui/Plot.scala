package regressionmodel.gui

import regressionmodel.PVector
import regressionmodel.mathematics.{ExponentialRegression, LinearRegression}
import scalafx.application.Platform
import scalafx.scene.chart.{LineChart, NumberAxis}
import scalafx.scene.layout.StackPane
import scalafx.util.StringConverter

import java.text.DecimalFormat

object Plot extends StackPane {

  var dataPoints: Array[PVector] = new Array[PVector](0)

  //Define both x and y axis and their number formats
  val decimalFormat = new DecimalFormat("#.#E0")
  val scientificConverter = new StringConverter[Number]() {
    override def toString(number: Number): String = decimalFormat.format(number.doubleValue())

    override def fromString(string: String): Number = {
      try decimalFormat.parse(string)
      catch {
        case e: Throwable =>
          println(e.getMessage)
          0
      }
    }
  }
  val xAxis: NumberAxis = new NumberAxis(-10, 10, 1) {
    animated = false
    label = "X-axis"
    minorTickCount = 5
    lowerBound.onChange {
      setTickUnit(this)
    }
    upperBound.onChange {
      setTickUnit(this)
    }
  }
  val yAxis: NumberAxis = new NumberAxis(-10, 10, 1) {
    animated = false
    label = "Y-axis"
    minorTickCount = 5
    lowerBound.onChange {
      setTickUnit(this)
    }
    upperBound.onChange {
      setTickUnit(this)
    }
  }

  val pointSeries: DataPointSeries = new DataPointSeries("Points")
  val regressionSeries: RegressionSeries = new RegressionSeries("Regression")
  val lineChart: LineChart[Number, Number] = new LineChart[Number, Number](this.xAxis, this.yAxis) {
    title = "Regression model by Matias"
    animated = false
  }
  this.children = lineChart


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
    // Remove all data from linechart because othervice the line strokes will be left visible.
    this.lineChart.getData.clear()
    this.lineChart.getData.addAll(this.pointSeries.series, this.regressionSeries.series)
    // Since LinearRegression and ExponentialRegression are both objects we can clear their values this way
    LinearRegression.clearAll()
    ExponentialRegression.clearAll()
    // Update bottom info labels
    BottomPanel.updateRSquared()
    BottomPanel.updateFunctionLabel()
    // Update the limits
    this.updateLimits()
  }


  //This should be checked everytime we change XY / YX format and when data changes
  def checkForDuplicates: (Boolean, Boolean) = {
    val xyDuplicates = this.dataPoints.groupBy(_.x).exists(_._2.length > 1)
    val yxDuplicates = this.dataPoints.groupBy(_.y).exists(_._2.length > 1)
    (xyDuplicates, yxDuplicates)
  }


  def updateLimits(): Unit = {
    this.limitsHelper(PlotLimits.xMin, PlotLimits.xMax, this.xAxis)
    this.limitsHelper(PlotLimits.yMin, PlotLimits.yMax, this.yAxis)
  }

  private def limitsHelper(limA: Option[Double], limB: Option[Double], axis: NumberAxis): Unit = {
    val xAxisBool = this.xAxis == axis
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
            if (xAxisBool) {
              axis.lowerBound = dataPoints.minBy(_.x).x
              axis.upperBound = dataPoints.maxBy(_.x).x
              val axisLength = axis.upperBound() - axis.lowerBound()
              axis.lowerBound = math.floor(axis.lowerBound() - axisLength / 50)
              axis.upperBound = math.ceil(axis.upperBound() + axisLength / 50)
            } else {
              // Y Axis may be autoranging
              axis.autoRanging = true
            }
          } else {
            // We'll pad +5 and -5 around this axis since all values are equal
            val head = this.dataPoints.head
            axis.lowerBound = math.floor((if (xAxisBool) head.x else head.y) - 5)
            axis.upperBound = math.ceil((if (xAxisBool) head.x else head.y) + 5)
          }
        } else {
          // If there's no points available we'll use default -10 and 10
          axis.lowerBound = -10
          axis.upperBound = 10
        }
    }
  }

  def setTickUnit(axis: NumberAxis): Unit = {
    val diff = axis.upperBound() - axis.lowerBound()
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
    Platform.runLater(
    if (diff > 1E5 || diff < 1E-4) {
      axis.tickLabelFormatter = scientificConverter
    } else {
      axis.tickLabelFormatter = NumberAxis.DefaultFormatter(axis)
    })
  }
}
