package regressionmodel.mathematics

import regressionmodel.PVector

class RegressionModel(data: Array[PVector]) {

  //These will be the coefficients for our graph
  //y = mx + b
  //y = m^x + b
  var m = 0D
  var b = 0D

  protected def getXValues: Array[Double] = this.data.map(_.x)

  protected def getYValues: Array[Double] = this.data.map(_.y)

  def calculateCoefficients(): Unit = {
    //This works for linear model, we can override it for exponential model later
    val xs = this.getXValues
    val ys = this.getYValues
    val xAvg:Double = xs.sum / xs.length
    val yAvg:Double = ys.sum / ys.length
    val nominator = xs.indices.map(i => (xs(i)-xAvg)*(ys(i)-yAvg)).sum
    val denominator = xs.indices.map(i => (xs(i)-xAvg)*(xs(i)-xAvg)).sum
    this.m = nominator / denominator
    //y = mx + b
    //b = y - mx
    this.b = yAvg - this.m * xAvg
  }

  def getCoefficients: (Double, Double) = (this.m, this.b)
}
