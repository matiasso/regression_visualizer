package regressionmodel.mathematics

import regressionmodel.PVector

trait RegressionModel {

  //This trait will help us in Linear and Exponential regression models
  //y = mx + b
  //y = Be^(mx) where B = e^b (meaning B > 0) and ln(y) = ln(b) + x*ln(m) is linear

  protected var data: Array[PVector] = Array[PVector]()

  def setData(arr: Array[PVector]): Unit = this.data = arr  // Mainly used for testing...

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

  protected def getXlogs: Array[Double] = data.map(p => math.log(p.x))

  protected def getYlogs: Array[Double] = data.map(p => math.log(p.y))

  def calculateCoefficients()

  def getCoefficients: (Option[Double], Option[Double]) = (this.m, this.b)

  protected def throwZeroWarning(): Nothing = {
    throw new Exception("All of the X values were equal, impossible to fit a regression line!")
  }
}
