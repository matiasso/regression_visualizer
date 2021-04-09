package regressionmodel.gui

import regressionmodel.{GlobalVars, PVector}
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{NumberAxis, ScatterChart}
import scalafx.scene.layout.StackPane

object Plot extends StackPane {

  val dataPoints: ObservableBuffer[PVector] = ObservableBuffer[PVector]()
  dataPoints.onChange((buffer, changes) => {
    // Everytime we remove or add to our dataPoints
    /*println(buffer)
    println(changes)*/
    this.update()
  })
  //Define both x and y axis
  val xAxis = new NumberAxis(-10, 10, 1)
  xAxis.setLabel("X-axis")
  xAxis.minorTickCount = 5
  val yAxis = new NumberAxis(-10, 10, 1)
  yAxis.setLabel("Y-axis")
  yAxis.minorTickCount = 5
  val pointSeries: DataPointSeries = new DataPointSeries("Points")
  val regressionSeries: RegressionSeries = new RegressionSeries("Regression")
  val scatterChart: ScatterChart[Number, Number] = ScatterChart[Number, Number](this.xAxis, this.yAxis)
  scatterChart.setTitle("Regression model by Matias")
  scatterChart.getData.addAll(pointSeries.series, regressionSeries.series)
  this.children = scatterChart


  def update(): Unit = {
    if (this.dataPoints.length == 0) {
      //If it's empty, we can simply remove our data
      this.clearPlot()
    } else {
      this.pointSeries.update()
    }
    // The last thing is to apply styles (We have to do this each time)
    this.pointSeries.applyStyles()
  }

  def updateRegressionSeries(): Unit = {
    if (this.dataPoints.length > 0) {
      this.regressionSeries.update()
      this.regressionSeries.applyStyles()
    }
  }

  @deprecated("Reason: There's really no need to update both at the same time...")
  private def applyStyles(): Unit = {
    this.pointSeries.applyStyles()
    this.regressionSeries.applyStyles()
  }

  def clearPlot(): Unit = {
    this.pointSeries.clear()
    this.regressionSeries.clear()
    BottomPanel.labelFunc.text = GlobalVars.labelUnknownText
    BottomPanel.labelRSquared.text = GlobalVars.labelUnknownText
    // If the user hasn't specified any limits, we'll make both axis in range [-10, 10]
    this.updateLimits()
  }


  //This should be checked everytime we change XY / YX format and when data changes
  def checkForDuplicates(leftX: Boolean): Boolean = {
    val duplicatesFound = if (leftX) {
      this.dataPoints.exists(p1 => this.dataPoints.exists(p2 => p2.first == p1.first && p2.second != p1.second))
    } else {
      this.dataPoints.exists(p1 => this.dataPoints.exists(p2 => p2.second == p1.second && p2.first != p1.first))
    }
    if (duplicatesFound) {
      Dialogs.showError("Duplicate Error",
        "There was duplicate values for same X-coordinate!",
        "Check your data or change between X;Y and Y;X formats!")
    }
    duplicatesFound
  }


  def updateLimits(): Unit = {
    //Update the limits to either given ones or the smallest and largest ones
    if (this.dataPoints.length == 1) {
      // This is mostly useless and nobody would want to test this but whatever
      // If there's exactly one point we want to pad around it
      val head = this.dataPoints.head
      this.xAxis.tickUnit = 1
      this.xAxis.lowerBound = (if (GlobalVars.leftCoordinateIsX) head.first else head.second) - 5
      this.xAxis.upperBound = (if (GlobalVars.leftCoordinateIsX) head.first else head.second) + 5
      // And the same for Y-axis
      this.yAxis.autoRanging = false
      this.yAxis.tickUnit = 1
      this.yAxis.lowerBound = (if (GlobalVars.leftCoordinateIsX) head.second else head.first) - 5
      this.yAxis.upperBound = (if (GlobalVars.leftCoordinateIsX) head.second else head.first) + 5
    } else {
      (PlotLimits.xMin, PlotLimits.xMax) match {
        case (Some(a), Some(b)) =>
          this.xAxis.lowerBound = a
          this.xAxis.upperBound = b
        case _ =>
          if (this.dataPoints.length > 1) {
            this.xAxis.lowerBound = if (GlobalVars.leftCoordinateIsX)
              this.dataPoints.minBy(_.first).first else this.dataPoints.minBy(_.second).second
            this.xAxis.lowerBound = this.xAxis.getLowerBound - math.abs(this.xAxis.getLowerBound) / 50
            this.xAxis.upperBound = if (GlobalVars.leftCoordinateIsX)
              this.dataPoints.maxBy(_.first).first else this.dataPoints.maxBy(_.second).second
            this.xAxis.upperBound = this.xAxis.getUpperBound + math.abs(this.xAxis.getUpperBound) / 50
          } else {
            this.xAxis.lowerBound = -10
            this.xAxis.upperBound = 10
          }
      }
      this.setTickUnit(this.xAxis)
      //Then the same for Y-axis
      //Set the defaults to -10 and 10
      this.yAxis.lowerBound = -10
      this.yAxis.upperBound = 10
      (PlotLimits.yMin, PlotLimits.yMax) match {
        case (Some(a), Some(b)) =>
          this.yAxis.autoRanging = false
          this.yAxis.lowerBound = a
          this.yAxis.upperBound = b
          this.setTickUnit(this.yAxis)
        case _ =>
          this.yAxis.autoRanging = true
      }
    }
  }

  def setTickUnit(axis: NumberAxis): Unit = {
    val diff = axis.getUpperBound - axis.getLowerBound
    if (diff > 20) {
      axis.tickUnit = math.round(diff / 20)
    } else if (diff >= 8) {
      axis.tickUnit = 1
    } else {
      axis.tickUnit = diff / 20
    }
  }
}
