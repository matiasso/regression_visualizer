package regressionmodel.mathematics


object LinearRegression extends RegressionModel {

  override def calculateCoefficients(): Unit = {
    //This works for linear model, we can override it for exponential model later
    val xs = this.getXValues
    val ys = this.getYValues
    val xAvg: Double = xs.sum / xs.length
    val yAvg: Double = ys.sum / ys.length
    val nominator = xs.indices.map(i => (xs(i) - xAvg) * (ys(i) - yAvg)).sum
    val denominator = xs.indices.map(i => (xs(i) - xAvg) * (xs(i) - xAvg)).sum
    this.m = nominator / denominator
    //y = mx + b
    //b = y - mx
    this.b = yAvg - this.m * xAvg
  }
}
