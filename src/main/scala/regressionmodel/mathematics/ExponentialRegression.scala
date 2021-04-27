package regressionmodel.mathematics


class ExponentialRegression extends RegressionModel {

  override def calculateCoefficients(): Unit = {
    this.clearAll() // Clear existing m, b, R^2 values
    val xs = this.getXValues
    val yValues =this.getYValues
    // We cannot take logarithms if some values of Y are negative or zero
    if (yValues.forall(_ > 0)) {
      // Take logarithms and fit a linear model for them
      // Here we could use the LinearRegression model again, but I decided to "repeat" a few lines here
      val ys = this.getYlogs
      val xAvg: Double = xs.iterator.sum / xs.length
      val yAvg: Double = ys.iterator.sum / ys.length

      val nominator = xs.indices.map(i => (xs(i) - xAvg) * (ys(i) - yAvg)).sum          // Sum((x-x_avg)*(y-y_avg))
      val denominator = xs.indices.map(i => xs(i) - xAvg).iterator.map(n => n * n).sum  // Sum((x-x_avg)^2)
      if (denominator != 0) {
        this.m = Some(nominator / denominator)
        this.m match {
          case Some(mVal) =>
            val helperB = yAvg - mVal * xAvg
            // Now ln(y)=mx+b
            // We want y=e^(mx+b)=e^b*e^(mx)=B*e^(mx) where B = e^b
            this.b = Some(math.exp(helperB))

            // Calculate R squared by sum(f_i - y_avg)^2 / sum(y_i - y_avg)^2
            // For the exponential model we'll compare it to the ln(y)=mx+b model
            val rNominator = xs.indices.map(i => mVal * xs(i) + helperB - yAvg).iterator.map(n => n * n).sum
            val rDenominator = ys.indices.map(i => ys(i) - yAvg).iterator.map(n => n * n).sum
            if (rDenominator != 0) {
              this.rSquared = Some(math.min(rNominator / rDenominator, 1.0))
            } else
              this.rSquared = Some(1.0)  // If all y-values are the same as yAvg, then our model fits perfectly with y=B*e^(0x)=B

          case None => println("m was NOT defined for some reason, even though it SHOULD be!")
        }
      } else {
        this.throwZeroWarning()
      }
    } else {
      throw new Exception("Some Y-values were below or equal to 0. Couldn't calculate exponential regression!")
    }
  }
}
