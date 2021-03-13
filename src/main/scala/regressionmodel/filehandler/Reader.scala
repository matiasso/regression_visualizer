package regressionmodel.filehandler

import regressionmodel.PVector

trait Reader {
  var lines:Array[String]
  def load()
  def getDataPoints(leftIsX: Boolean): Array[PVector]
}
