package regressionmodel.gui

import org.scalafx.extras.offFXAndWait
import regressionmodel.GlobalVars
import regressionmodel.mathematics._
import scalafx.scene.chart.XYChart

class RegressionSeries(name: String) extends PointSeries(name) {

  val index = 1
  var regressionObject: RegressionModel = LinearRegression // As a default this will be linear regression

  private def isLinear: Boolean = this.regressionObject == LinearRegression

  override def update(): Unit = {
    this.series.getData.clear()
    if (Plot.dataPoints.length > 1) {
      offFXAndWait {
        regressionObject.calculateCoefficients(GlobalVars.leftCoordinateIsX)
      }
      val coefficients: (Option[Double], Option[Double]) = regressionObject.getCoefficients
      //Update the labels to show this regression line and R^2 value
      BottomPanel.updateFunctionLabel(coefficients, this.isLinear)
      BottomPanel.updateRSquared(regressionObject.rSquared)
      coefficients match {
        case (Some(m), Some(b)) =>
          //Add the dots for the regressionModel
          val start = PlotLimits.xMin.getOrElse(Plot.xAxis.getLowerBound)
          val end = PlotLimits.xMax.getOrElse(Plot.xAxis.getUpperBound)
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
      //We can't draw a regression line for 0 or 1 points
      println(s"Impossible to draw a regression line for ${Plot.dataPoints.length} points")
      Dialogs.showError("Regression line error",
        "Impossible to draw a regression line",
        s"Reason: Only ${Plot.dataPoints.length} points given!")
    }
  }
}
