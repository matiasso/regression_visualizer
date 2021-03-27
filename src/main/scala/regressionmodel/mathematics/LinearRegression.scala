package regressionmodel.mathematics

import regressionmodel.gui.Dialogs


object LinearRegression extends RegressionModel {

  override def calculateCoefficients(leftX: Boolean): Unit = {
    // This formula was found online.
    // Basic idea is "Sum(x_avg*y_avg) / Sum(x_avg^2)"
    this.m = None
    this.b = None
    val xs = if (leftX) this.getXValues else this.getYValues
    val ys = if (leftX) this.getYValues else this.getXValues
    val xAvg: Double = xs.sum / xs.length
    val yAvg: Double = ys.sum / ys.length
    val nominator = xs.indices.map(i => (xs(i) - xAvg) * (ys(i) - yAvg)).sum
    val denominator = xs.indices.map(i => xs(i) - xAvg).map(n => n * n).sum
    if (denominator != 0) {
      this.m = Some(nominator / denominator)
      //It would be safe to use "m.get" after this, but match case is probably better
      this.m match {
        case Some(mVal) =>
          //y = mx + b
          //b = y - mx
          this.b = Some(yAvg - mVal * xAvg)
          this.b match {
            case Some(bVal) =>
              //Then calculate R squared by sum(f_i - y_avg)^2 / sum(y_i - y_avg)^2
              //For the linear model f_i equals mx+b
              val rNominator = xs.indices.map(i => mVal * xs(i) + bVal - yAvg).map(n => n * n).sum
              val rDenominator = ys.indices.map(i => ys(i) - yAvg).map(n => n * n).sum
              if (rDenominator != 0)
                this.rSquared = Some(rNominator / rDenominator)

            case None => println("b was NOT defined for some odd reason!")
          }
        case None => println("m was NOT defined for some reason, even though it SHOULD be!")
      }
    } else {
      Dialogs.showWarning("Warning", "Zero division", "Could not calculate exponential regression!")
    }
  }
}
