package regressionmodel.gui

case object PlotLimits {
  var xMin: Option[Double] = None
  var xMax: Option[Double] = None
  var yMin: Option[Double] = None
  var yMax: Option[Double] = None

  def setLimitsX(limA: Option[Double], limB: Option[Double]): Unit = {
    this.xMin = limA
    this.xMax = limB
  }

  def setLimitsY(limA: Option[Double], limB: Option[Double]): Unit = {
    this.yMin = limA
    this.yMax = limB
  }
}
