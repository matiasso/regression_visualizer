package regressionmodel.gui

case object PlotLimits {

  // Here we hold the limits for X and Y axis. User can set or clear these via custom dialogs from settings.
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
