package regressionmodel.mathematics

import regressionmodel.gui.Dialogs


object ExponentialRegression extends RegressionModel {

  override def calculateCoefficients(leftX: Boolean): Unit = {
    this.m = None
    this.b = None
    //leftX boolean indicates whether "X;Y" coordinate pairs are given this way or the other way "Y;X"
    val xs = if (leftX) this.getXValues else this.getYValues
    val yValues = if (leftX) this.getYValues else this.getXValues
    //We cannot take logarithms if some values of Y are negative.
    if (yValues.forall(_ >= 0)) {
      //Take logarithms and fit a linear model for them
      val ys = if (leftX) this.getYlogs else this.getXlogs
      val xAvg: Double = xs.sum / xs.length
      val yAvg: Double = ys.sum / ys.length

      val nominator = xs.indices.map(i => (xs(i) - xAvg) * (ys(i) - yAvg)).sum
      val denominator = xs.indices.map(i => xs(i) - xAvg).map(n => n * n).sum
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
            val rNominator = xs.indices.map(i => mVal * xs(i) + helperB - yAvg).map(n => n * n).sum
            val rDenominator = ys.indices.map(i => ys(i) - yAvg).map(n => n * n).sum
            if (rDenominator != 0)
              this.rSquared = Some(rNominator / rDenominator)

          case None => println("m was NOT defined for some reason, even though it SHOULD be!")
        }
      } else {
        Dialogs.showWarning("Warning", "Zero division", "Could not calculate exponential regression!")
      }
    }
  }
}
