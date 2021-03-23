package regressionmodel.mathematics


object LinearRegression extends RegressionModel {

  override def calculateCoefficients(leftX: Boolean): Unit = {
    //This works for linear model, we can override it for exponential model later
    val xs = this.getXValues
    val ys = this.getYValues
    val xAvg: Double = xs.sum / xs.length
    val yAvg: Double = ys.sum / ys.length
    if (leftX){
      val nominator = xs.indices.map(i => (xs(i) - xAvg) * (ys(i) - yAvg)).sum
      val denominator = xs.indices.map(i => (xs(i) - xAvg) * (xs(i) - xAvg)).sum
      this.m = nominator / denominator
      //y = mx + b
      //b = y - mx
      this.b = yAvg - this.m * xAvg
    } else {
      val nominator = ys.indices.map(i => (ys(i) - yAvg) * (xs(i) - xAvg)).sum
      val denominator = ys.indices.map(i => (ys(i) - yAvg) * (ys(i) - yAvg)).sum
      this.m = nominator / denominator
      this.b = xAvg - this.m * yAvg
    }
  }
}
