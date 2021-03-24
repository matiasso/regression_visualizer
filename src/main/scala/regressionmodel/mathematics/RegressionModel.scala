package regressionmodel.mathematics

import regressionmodel.gui.Plot

trait RegressionModel {

  //These will be the coefficients for our graph
  //y = mx + b
  //y = Be^(mx) where B = e^b and ln(y) = ln(b) + x*ln(m) is linear
  var m:Option[Double] = None
  var b:Option[Double] = None
  var rSquared:Option[Double] = None

  protected def getXValues: Array[Double] = Plot.dataPoints.map(_.x).toArray

  protected def getYValues: Array[Double] = Plot.dataPoints.map(_.y).toArray

  protected def getXlogs: Array[Double] = Plot.dataPoints.map(p => math.log(p.x)).toArray

  protected def getYlogs: Array[Double] = Plot.dataPoints.map(p => math.log(p.y)).toArray

  def calculateCoefficients(leftX: Boolean)

  def getCoefficients: (Option[Double], Option[Double]) = (this.m, this.b)
}
