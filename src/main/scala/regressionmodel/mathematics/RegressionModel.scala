package regressionmodel.mathematics

import regressionmodel.gui.Plot

trait RegressionModel {

  //These will be the coefficients for our graph
  //y = mx + b
  //y = m^x + b
  var m = 0D
  var b = 0D

  protected def getXValues: Array[Double] = Plot.dataPoints.map(_.x).toArray

  protected def getYValues: Array[Double] = Plot.dataPoints.map(_.y).toArray

  def calculateCoefficients()

  def getCoefficients: (Double, Double) = (this.m, this.b)
}
