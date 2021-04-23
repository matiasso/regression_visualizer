package regressionmodel.gui

import org.scalafx.extras.{offFXAndWait, onFX}
import regressionmodel.mathematics._
import scalafx.scene.chart.XYChart

class RegressionSeries(name: String) extends PointSeries(name) {

  val index = 1
  var regressionInstance: RegressionModel = new LinearRegression()

  def isLinear: Boolean = {
    this.regressionInstance match {
      case _: LinearRegression => true
      case _ => false
    }
  }

  override def update(): Unit = {
    this.clear()
    if (Plot.dataPoints.length > 1) {
      offFXAndWait {
        try {
          regressionInstance.setData(Plot.dataPoints)
          regressionInstance.calculateCoefficients()
        } catch {
          case e: Throwable =>
            onFX {
              Dialogs.showWarning("Regression warning",
                e.getMessage,
                "Please check your data!")
            }
        }
      }
      val coefficients: (Option[Double], Option[Double]) = regressionInstance.getCoefficients
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
            case _ => this.clear()
          }
          val width = end - start
          //This will specify how often the dots for regression line are drawn
          val iterations = 300
          for (i <- 0 to iterations) {
            val x = start + i * width / iterations
            val y = if (this.isLinear) m * x + b else b * math.exp(m * x)
            if (y.isFinite)
              this.series.getData.add(XYChart.Data(x, y))
          }
        case _ =>
          Plot.lineChart.getData.remove(this.series)
          Plot.lineChart.getData.add(this.series)
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
