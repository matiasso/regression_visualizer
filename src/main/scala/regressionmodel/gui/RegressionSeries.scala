package regressionmodel.gui

import org.scalafx.extras.offFXAndWait
import regressionmodel.mathematics._
import scalafx.scene.chart.XYChart

class RegressionSeries(name: String) extends PointSeries(name) {

  val index = 1
  var regressionObject: RegressionModel = LinearRegression // As a default this will be linear regression

  def isLinear: Boolean = this.regressionObject == LinearRegression

  override def update(): Unit = {
    this.clear()
    if (Plot.dataPoints.length > 1) {
      offFXAndWait {
        regressionObject.calculateCoefficients()
      }
      val coefficients: (Option[Double], Option[Double]) = regressionObject.getCoefficients
      //Update the labels to show this regression line and R^2 value
      BottomPanel.updateFunctionLabel()
      BottomPanel.updateRSquared()
      coefficients match {
        case (Some(m), Some(b)) =>
          //Add the dots for the regressionModel
          var start = PlotLimits.xMin.getOrElse(Plot.xAxis.lowerBound())
          var end = PlotLimits.xMax.getOrElse(Plot.xAxis.upperBound())
          (PlotLimits.yMin, PlotLimits.yMax) match {
            case (Some(min), Some(max)) =>
              if (this.isLinear) {
                // We want to solve smallest x that has corresponding y-coordinate still visible
                // y = mx + b   -->   x = (y-b) / m
                if (m != 0) {
                  val xLims = Seq((min - b) / m, (max - b) / m)
                  start = math.max(start, xLims.min)
                  end = math.min(end, xLims.max)
                }
              } else {
                // y = b * e^(mx) -->   ln (y/b) / m
                if (m != 0 && b != 0) {
                  val xLims = Seq(math.log(min / b) / m, math.log(max / b) / m)
                  start = math.max(start, xLims.min)
                  end = math.min(end, xLims.max)
                }
              }
            case _ =>
          }
          val width = end - start
          //This will specify how often the dots for regression line are drawn
          val iterations = 500
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
      val pointStr = if (Plot.dataPoints.length == 1) "point" else "points"
      println(s"Impossible to draw a regression line for ${Plot.dataPoints.length} $pointStr")
      Dialogs.showError("Regression line error",
        "Impossible to draw a regression line",
        s"Reason: Only ${Plot.dataPoints.length} $pointStr given!")
    }
  }
}
