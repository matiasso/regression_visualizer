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
  xAxis.setLabel("X")
  xAxis.autoRanging = true
  val yAxis = new NumberAxis(-10, 10, 1)
  yAxis.setLabel("Y")
  yAxis.autoRanging = true
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
    }
    else if (!this.checkForDuplicates) {
      // Check that there are no duplicates with same X value but different Y value
      // If everything is as it should be, we update the points
      this.pointSeries.update()
      this.regressionSeries.update()
    }
    // The last thing is to apply styles (We have to do this each time)
    this.pointSeries.applyStyles()
    this.regressionSeries.applyStyles()
  }

  def clearPlot(): Unit = {
    this.pointSeries.series.getData.clear()
    this.regressionSeries.series.getData.clear()
  }


  //This should be checked everytime we change XY / YX format and when data changes
  def checkForDuplicates: Boolean = {
    val duplicatesFound = if (GlobalVars.leftCoordinateIsX) {
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


  def setLimitsX(limA: Option[Double], limB: Option[Double]): Unit = {
    PlotLimits.xMin = limA
    PlotLimits.xMax = limB
    (limA, limB) match {
      case (Some(a), Some(b)) =>
        this.xAxis.autoRanging = false
        this.xAxis.lowerBound = a
        this.xAxis.upperBound = b
        this.xAxis.tickUnit = math.abs(a - b) / 20
      case _ => this.xAxis.autoRanging = true
    }
    this.regressionSeries.update()
  }

  def setLimitsY(limA: Option[Double], limB: Option[Double]): Unit = {
    PlotLimits.yMin = limA
    PlotLimits.yMax = limB
    (limA, limB) match {
      case (Some(a), Some(b)) =>
        this.yAxis.autoRanging = false
        this.yAxis.lowerBound = a
        this.yAxis.upperBound = b
        this.yAxis.tickUnit = math.abs(a - b) / 10
      case _ => this.yAxis.autoRanging = true
    }
    this.regressionSeries.update()
  }

  //This is/was mainly used for debugging purposes
  def addPoint(point: PVector): Unit = {
    this.dataPoints.add(point)
  }
}
