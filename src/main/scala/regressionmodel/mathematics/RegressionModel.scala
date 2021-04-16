package regressionmodel.mathematics

import regressionmodel.gui.{Dialogs, Plot}
import scalafx.scene.control.ButtonType

trait RegressionModel {

  //These will be the coefficients for our graph
  //y = mx + b
  //y = Be^(mx) where B = e^b (meaning B > 0) and ln(y) = ln(b) + x*ln(m) is linear
  var m: Option[Double] = None
  var b: Option[Double] = None
  var rSquared: Option[Double] = None

  def clearAll(): Unit = {
    this.m = None
    this.b = None
    this.rSquared = None
  }

  protected def getXValues: Array[Double] = Plot.dataPoints.map(_.x)

  protected def getYValues: Array[Double] = Plot.dataPoints.map(_.y)

  protected def getXlogs: Array[Double] = Plot.dataPoints.map(p => math.log(p.x))

  protected def getYlogs: Array[Double] = Plot.dataPoints.map(p => math.log(p.y))

  def calculateCoefficients()

  def getCoefficients: (Option[Double], Option[Double]) = (this.m, this.b)

  protected def showZeroWarning(): Option[ButtonType] = {
    Dialogs.showWarning("Warning",
      "All of your X values were equal to each other",
      "Impossible to fit a regression line!")
  }
}
