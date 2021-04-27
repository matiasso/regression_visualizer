package regressionmodel.mathematics


class LinearRegression extends RegressionModel {

  override def calculateCoefficients(): Unit = {
    // This formula was found online. (From a video https://www.youtube.com/watch?v=szXbuO3bVRk )
    // Basic idea is "Sum((x-x_avg)*(y-y_avg)) / Sum((x-x_avg)^2)"
    this.clearAll() // Clear existing m, b, R^2 values
    val xs = this.getXValues
    val ys = this.getYValues
    val xAvg: Double = xs.iterator.sum / xs.length
    val yAvg: Double = ys.iterator.sum / ys.length
    val nominator = xs.indices.map(i => (xs(i) - xAvg) * (ys(i) - yAvg)).sum  // Sum((x-x_avg)*(y-y_avg))
    val denominator = xs.indices.map(i => xs(i) - xAvg).map(n => n * n).sum   // Sum((x-x_avg)^2)
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
              //For the linear model f_i=mx+b
              val rNominator = xs.indices.map(i => mVal * xs(i) + bVal - yAvg).iterator.map(n => n * n).sum   //sum(f_i - y_avg)^2
              val rDenominator = ys.indices.map(i => ys(i) - yAvg).iterator.map(n => n * n).sum               //sum(y_i - y_avg)^2
              if (rDenominator != 0)
                this.rSquared = Some(math.min(rNominator / rDenominator, 1.0))
              else
                this.rSquared = Some(1.0) // Since all Y values are the same as yAvg, our y=0x+b line fits perfectly

            case None => println("b was NOT defined for some odd reason!")
          }
        case None => println("m was NOT defined for some reason, even though it SHOULD be!")
      }
    } else {
      this.throwZeroWarning()
    }
  }
}
