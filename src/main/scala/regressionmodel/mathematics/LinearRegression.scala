package regressionmodel.mathematics


object LinearRegression extends RegressionModel {

  override def calculateCoefficients(leftX: Boolean): Unit = {
    // This formula was found online.
    // Basic idea is "Sum(x_avg*y_avg) / Sum(x_avg^2)"
    this.m = None
    this.b = None
    val xs = if (leftX) this.getXValues else this.getYValues
    val ys = if (leftX) this.getYValues else this.getYValues
    val xAvg: Double = if (leftX) xs.sum / xs.length else ys.sum / ys.length
    val yAvg: Double = if (leftX) ys.sum / ys.length else xs.sum / xs.length
    val nominator = xs.indices.map(i => (xs(i) - xAvg) * (ys(i) - yAvg)).sum
    val denominator = xs.indices.map(i => (xs(i) - xAvg) * (xs(i) - xAvg)).sum
    if (denominator != 0){
      this.m = Some(nominator / denominator)
      //y = mx + b
      //b = y - mx
      //It is safe to use .get here since m is defined for sure
      this.b = Some(yAvg - this.m.get * xAvg)
    } else {
      //Show warning!
    }
  }
}
