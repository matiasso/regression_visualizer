package regressionmodel.gui

import regressionmodel.PVector
import scalafx.application.Platform
import scalafx.scene.chart.{LineChart, NumberAxis}
import scalafx.scene.layout.StackPane
import scalafx.util.StringConverter

import java.text.DecimalFormat

object Plot extends StackPane {

  var dataPoints: Array[PVector] = Array[PVector]()

  // Define both x and y axis and their number formats that are used for scientific form
  private val decimalFormat = new DecimalFormat("#.#E0")
  private val scientificConverter: StringConverter[Number] = new StringConverter[Number]() {
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
  this.lineChart.getData.addAll(this.pointSeries.series, this.regressionSeries.series)
  this.children = lineChart


  def updateDataPoints(): Unit = {
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
    // Remove all data from lineChart because otherwice the line strokes will be left visible.
    this.lineChart.getData.clear()
    this.lineChart.getData.addAll(this.pointSeries.series, this.regressionSeries.series)
    this.regressionSeries.regressionInstance.clearAll()
    // Update bottom info labels
    BottomPanel.updateRSquared()
    BottomPanel.updateFunctionLabel()
    // Update the limits
    this.updateLimits()
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
          // Check whether there are DIFFERENT UNIQUE values on this axis
          if (axisValues.size > 1) {
            if (xAxisBool) {
              axis.lowerBound = dataPoints.minBy(_.x).x
              axis.upperBound = dataPoints.maxBy(_.x).x
              val axisLength = axis.upperBound() - axis.lowerBound()
              // Pad a bit around both ends
              axis.lowerBound = math.floor(axis.lowerBound() - axisLength / 80)
              axis.upperBound = math.ceil(axis.upperBound() + axisLength / 80)
            } else {
              // Y Axis may be autoRanging
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

  private def setTickUnit(axis: NumberAxis): Unit = {
    val diff = math.abs(axis.upperBound() - axis.lowerBound())
    if (diff.isFinite) {
      if (diff > 20) {
        axis.tickUnit = diff / 20
      } else if (diff >= 7) {
        axis.tickUnit = 1
      } else if (diff >= 2) {
        axis.tickUnit = 0.25
      } else {
        // This will sometimes show weird values
        axis.tickUnit = diff / 20
      }
    } else {
      axis.tickUnit = Double.MaxValue / 10 // For some reason this doesn't work as expected, ScalaFX still tries to draw + 2000 ticks and prints warnings
    }

    // Check whether we want to use scientific form or normal form for the tick-labels
    Platform.runLater(
      if (diff.isInfinite || diff > 1E6 || diff < 1E-5) {
        axis.tickLabelFormatter = scientificConverter
      } else {
        axis.tickLabelFormatter = NumberAxis.DefaultFormatter(axis)
      })
  }
}
