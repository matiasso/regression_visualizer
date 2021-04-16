package regressionmodel.mathematics

import org.scalafx.extras.onFX
import regressionmodel.gui.Dialogs


object ExponentialRegression extends RegressionModel {

  override def calculateCoefficients(): Unit = {
    this.clearAll() // Clear existing m, b, R^2 values
    val xs = this.getXValues
    val yValues = this.getYValues
    //We cannot take logarithms if some values of Y are negative or zero
    if (yValues.forall(_ > 0)) {
      //Take logarithms and fit a linear model for them
      val ys = this.getYlogs
      val xAvg: Double = xs.iterator.sum / xs.length
      val yAvg: Double = ys.iterator.sum / ys.length

      val nominator = xs.indices.map(i => (xs(i) - xAvg) * (ys(i) - yAvg)).sum
      val denominator = xs.indices.map(i => xs(i) - xAvg).iterator.map(n => n * n).sum
      if (denominator != 0) {
        this.m = Some(nominator / denominator)
        this.m match {
          case Some(mVal) =>
            val helperB = yAvg - mVal * xAvg
            //Now ln(y)=mx+b
            //We want y=e^(mx+b)=e^b*e^(mx)=B*e^(mx)
            this.b = Some(math.exp(helperB))

            //Then calculate R squared by sum(f_i - y_avg)^2 / sum(y_i - y_avg)^2
            //For the exponential model we'll compare it to the ln(y)=mx+b model
            val rNominator = xs.indices.map(i => mVal * xs(i) + helperB - yAvg).iterator.map(n => n * n).sum
            val rDenominator = ys.indices.map(i => ys(i) - yAvg).iterator.map(n => n * n).sum
            if (rDenominator != 0)
              this.rSquared = Some(math.min(rNominator / rDenominator, 1.0))
            else
              this.rSquared = Some(1.0)

          case None => println("m was NOT defined for some reason, even though it SHOULD be!")
        }
      } else {
        onFX {
          this.showZeroWarning()
        }
      }
    } else {
      onFX {
        Dialogs.showWarning("Warning",
          "Some Y-values were below or equal to 0",
          "Could not calculate exponential regression!")
      }
    }
  }
}
