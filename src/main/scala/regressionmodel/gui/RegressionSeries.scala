package regressionmodel.gui

import regressionmodel.GlobalVars
import regressionmodel.mathematics._
import scalafx.scene.chart.XYChart

class RegressionSeries(name: String) extends PointSeries(name) {

  val index = 1
  var regressionObject: RegressionModel = LinearRegression // As a default this will be linear regression

  private def isLinear: Boolean = this.regressionObject == LinearRegression

  override def update(): Unit = {
    this.series.getData.clear()
    if (Plot.dataPoints.length > 1){
      regressionObject.calculateCoefficients(GlobalVars.leftCoordinateIsX)
      val coefficients: (Option[Double], Option[Double]) = regressionObject.getCoefficients
      //Update the labels to show this regression line and R^2 value
      SidePanel.updateFunctionLabel(coefficients, this.isLinear)
      SidePanel.updateRSquared(regressionObject.rSquared)
      coefficients match {
        case (Some(m), Some(b)) =>
          //Add the dots for the regressionModel
          val points = Plot.dataPoints.map(p => if (GlobalVars.leftCoordinateIsX) p.first else p.second)
          //As default we'll draw the regression line with dots equally across
          //From the smallest x-coordinate to the largest x-coordinate
          var start: Double = points.min
          var end: Double = points.max
          //Check if the user has given custom xMin and xMax limits
          (PlotLimits.xMin, PlotLimits.xMax) match {
            case (Some(a), Some(b)) =>
              if (a > start) {
                //If the given xMin is bigger than smallest x, we will draw the lines on the visible part
                start = a
              }
              if (b < end) {
                //If the given xMax is smaller than biggest x, the line will be drawn only on the visible part
                end = b
              }
            case _ => // No need to adjust the start and end values
          }
          val width = end - start
          //This will specify how often the dots for regression line are drawn
          val iterations = 300
          for (i <- 0 to iterations) {
            val x = start + i * width / iterations
            if (this.isLinear) {
              this.series.getData.add(XYChart.Data(x, m * x + b))
            } else {
              this.series.getData.add(XYChart.Data(x, b * math.exp(m * x)))
            }
          }
        case _ => println("Coefficients were NONE for the regression line")
      }
    } else {
      //We can't draw a regression line for 0 or 1 points so clear any
      println(s"Impossible to draw a regression line for ${Plot.dataPoints.length} points")
    }
  }
}
