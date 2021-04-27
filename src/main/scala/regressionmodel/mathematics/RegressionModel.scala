package regressionmodel.mathematics

import regressionmodel.PVector

trait RegressionModel {

  // This trait will help us in Linear and Exponential regression models
  // Linear:       y = mx + b
  // Exponential:  y = Be^(mx) where B = e^b (meaning B > 0) and ln(y) = ln(b) + x*ln(m) is linear

  protected var data: Array[PVector] = Array[PVector]()

  // Since traits cannot be instantiated with parameters, I had to do this. This also makes scalatests easier to write.
  def setData(arr: Array[PVector]): Unit = this.data = arr

  var m: Option[Double] = None
  var b: Option[Double] = None
  var rSquared: Option[Double] = None

  def clearAll(): Unit = {
    this.m = None
    this.b = None
    this.rSquared = None
  }

  protected def getXValues: Array[Double] = data.map(_.x)

  protected def getYValues: Array[Double] = data.map(_.y)

  protected def getYlogs: Array[Double] = data.map(p => math.log(p.y))

  def calculateCoefficients(): Unit

  def getCoefficients: (Option[Double], Option[Double]) = (this.m, this.b)

  protected def throwZeroWarning(): Nothing = {
    throw new Exception("All of the X values were equal, impossible to fit a regression line!")
  }
}
